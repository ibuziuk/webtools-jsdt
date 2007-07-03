/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.internal.compiler.ast;

import org.eclipse.wst.jsdt.core.JavaCore;
import org.eclipse.wst.jsdt.internal.compiler.ASTVisitor;
import org.eclipse.wst.jsdt.internal.compiler.impl.*;
import org.eclipse.wst.jsdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.wst.jsdt.internal.compiler.codegen.*;
import org.eclipse.wst.jsdt.internal.compiler.flow.*;
import org.eclipse.wst.jsdt.internal.compiler.lookup.*;

public class LocalDeclaration extends AbstractVariableDeclaration {

	public LocalVariableBinding binding;
	
	public LocalDeclaration(
		char[] name,
		int sourceStart,
		int sourceEnd) {

		this.name = name;
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		this.declarationEnd = sourceEnd;
	}

public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
	// record variable initialization if any
	if ((flowInfo.tagBits & FlowInfo.UNREACHABLE) == 0) {
		bits |= ASTNode.IsLocalDeclarationReachable; // only set if actually reached
	}
	if (this.initialization == null) { 
		return flowInfo;
	}
	int nullStatus = this.initialization.nullStatus(flowInfo);
	flowInfo =
		this.initialization
			.analyseCode(currentScope, flowContext, flowInfo)
			.unconditionalInits();
	if (!flowInfo.isDefinitelyAssigned(this.binding)){// for local variable debug attributes
		this.bits |= FirstAssignmentToLocal;
	} else {
		this.bits &= ~FirstAssignmentToLocal;  // int i = (i = 0);
	}	
	flowInfo.markAsDefinitelyAssigned(binding);
	if ((this.binding.type.tagBits & TagBits.IsBaseType) == 0) {
		switch(nullStatus) {
			case FlowInfo.NULL :
				flowInfo.markAsDefinitelyNull(this.binding);
				break;
			case FlowInfo.NON_NULL :
				flowInfo.markAsDefinitelyNonNull(this.binding);
				break;
			default:
				flowInfo.markAsDefinitelyUnknown(this.binding);
		}
		// no need to inform enclosing try block since its locals won't get
		// known by the finally block
	}
	return flowInfo;
}

	public void checkModifiers() {

		//only potential valid modifier is <<final>>
		if (((modifiers & ExtraCompilerModifiers.AccJustFlag) & ~ClassFileConstants.AccFinal) != 0)
			//AccModifierProblem -> other (non-visibility problem)
			//AccAlternateModifierProblem -> duplicate modifier
			//AccModifierProblem | AccAlternateModifierProblem -> visibility problem"

			modifiers = (modifiers & ~ExtraCompilerModifiers.AccAlternateModifierProblem) | ExtraCompilerModifiers.AccModifierProblem;
	}

	/**
	 * Code generation for a local declaration:
	 *	i.e.&nbsp;normal assignment to a local variable + unused variable handling 
	 */
	public void generateCode(BlockScope currentScope, CodeStream codeStream) {

		// even if not reachable, variable must be added to visible if allocated (28298)
		if (binding.resolvedPosition != -1) {
			codeStream.addVisibleLocalVariable(binding);
		}
		if ((bits & IsReachable) == 0) {
			return;
		}
		int pc = codeStream.position;

		// something to initialize?
		generateInit: {
			if (this.initialization == null) 
				break generateInit;
			// forget initializing unused or final locals set to constant value (final ones are inlined)
			if (binding.resolvedPosition < 0) {
				if (initialization.constant != Constant.NotAConstant) 
					break generateInit;
				// if binding unused generate then discard the value
				initialization.generateCode(currentScope, codeStream, false);
				break generateInit;
			}
			initialization.generateCode(currentScope, codeStream, true);
			// 26903, need extra cast to store null in array local var	
			if (binding.type.isArrayType() 
				&& (initialization.resolvedType == TypeBinding.NULL	// arrayLoc = null
					|| ((initialization instanceof CastExpression)	// arrayLoc = (type[])null
						&& (((CastExpression)initialization).innermostCastedExpression().resolvedType == TypeBinding.NULL)))){
				codeStream.checkcast(binding.type); 
			}					
			codeStream.store(binding, false);
			if ((this.bits & ASTNode.FirstAssignmentToLocal) != 0) {
				/* Variable may have been initialized during the code initializing it
					e.g. int i = (i = 1);
				*/
				binding.recordInitializationStartPC(codeStream.position);
			}
		}
		codeStream.recordPositionsFrom(pc, this.sourceStart);
	}

	/**
	 * @see org.eclipse.wst.jsdt.internal.compiler.ast.AbstractVariableDeclaration#getKind()
	 */
	public int getKind() {
		return LOCAL_VARIABLE;
	}
	
	public void resolve(BlockScope scope) {

		// create a binding and add it to the scope
		TypeBinding variableType = null;
			if (type!=null) 
				variableType=type.resolveType(scope, true /* check bounds*/); 
			else {
				if (inferredType!=null)
				  variableType=inferredType.resolveType(scope,this);
				else
					variableType=TypeBinding.UNKNOWN;
			}
 

		checkModifiers();
		if (variableType != null) {
			if (variableType == TypeBinding.VOID) {
				scope.problemReporter().variableTypeCannotBeVoid(this);
				return;
			}
			if (variableType.isArrayType() && ((ArrayBinding) variableType).leafComponentType == TypeBinding.VOID) {
				scope.problemReporter().variableTypeCannotBeVoidArray(this);
				return;
			}
		}
		
		Binding varBinding  = scope.getBinding(name, Binding.VARIABLE, this, false /*do not resolve hidden field*/);
		if (varBinding != null && varBinding.isValidBinding()){
			VariableBinding existingVariable=(VariableBinding)varBinding;
			if (existingVariable.isFor(this))
			{
				existingVariable.type=variableType;
			}
			else
			{

			if (existingVariable instanceof LocalVariableBinding && this.hiddenVariableDepth == 0) {
				scope.problemReporter().redefineLocal(this);
			} else {
				scope.problemReporter().localVariableHiding(this, existingVariable, false);
			}
			}
		}
				
		if ((modifiers & ClassFileConstants.AccFinal)!= 0 && this.initialization == null) {
			modifiers |= ExtraCompilerModifiers.AccBlankFinal;
		}
		this.binding = new LocalVariableBinding(this, variableType, modifiers, false);
		MethodScope methodScope = scope.methodScope();
		if (methodScope!=null)
			methodScope.addLocalVariable(binding);
		else
			scope.compilationUnitScope().addLocalVariable(binding);
		this.binding.setConstant(Constant.NotAConstant);
		// allow to recursivelly target the binding....
		// the correct constant is harmed if correctly computed at the end of this method

		if (variableType == null) {
			if (initialization != null)
				initialization.resolveType(scope); // want to report all possible errors
			return;
		}

		// store the constant for final locals 	
		if (initialization != null) {
			if (initialization instanceof ArrayInitializer) {
				TypeBinding initializationType = initialization.resolveTypeExpecting(scope, variableType);
				if (initializationType != null) {
					((ArrayInitializer) initialization).binding = (ArrayBinding) initializationType;
					initialization.computeConversion(scope, variableType, initializationType);
				}
			} else {
			    this.initialization.setExpectedType(variableType);
				TypeBinding initializationType = this.initialization.resolveType(scope);
				if (initializationType != null) {
//					if (variableType != initializationType) // must call before computeConversion() and typeMismatchError()
//						scope.compilationUnitScope().recordTypeConversion(variableType, initializationType);
					if (variableType==TypeBinding.UNKNOWN)
						this.binding.type=initializationType;
					else if (initialization.isConstantValueOfTypeAssignableToType(initializationType, variableType)
						|| variableType.isBaseType() /* && BaseTypeBinding.isWidening(variableType.id, initializationType.id)) */
						|| initializationType.isCompatibleWith(variableType)) {
						
						
//						this.initialization.computeConversion(scope, variableType, initializationType);
//						if (initializationType.needsUncheckedConversion(variableType)) {
//						    scope.problemReporter().unsafeTypeConversion(this.initialization, initializationType, variableType);
//						}						
//						if (this.initialization instanceof CastExpression 
//								&& (this.initialization.bits & ASTNode.UnnecessaryCast) == 0) {
//							CastExpression.checkNeedForAssignedCast(scope, variableType, (CastExpression) this.initialization);
//						}	
//					} else if (scope.isBoxingCompatibleWith(initializationType, variableType) 
//										|| (initializationType.isBaseType()  // narrowing then boxing ?
//												&& scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5 // autoboxing
//												&& !variableType.isBaseType()
//												&& initialization.isConstantValueOfTypeAssignableToType(initializationType, scope.environment().computeBoxingType(variableType)))) {
//						this.initialization.computeConversion(scope, variableType, initializationType);
//						if (this.initialization instanceof CastExpression 
//								&& (this.initialization.bits & ASTNode.UnnecessaryCast) == 0) {
//							CastExpression.checkNeedForAssignedCast(scope, variableType, (CastExpression) this.initialization);
//						}	
					} else {
						scope.problemReporter().typeMismatchError(initializationType, variableType, this.initialization);
					}
				}
			}
			// check for assignment with no effect
			if (this.binding == Assignment.getDirectBinding(this.initialization)) {
				scope.problemReporter().assignmentHasNoEffect(this, this.name);
			}
			// change the constant in the binding when it is final
			// (the optimization of the constant propagation will be done later on)
			// cast from constant actual type to variable type
			binding.setConstant(
				binding.isFinal()
					? initialization.constant.castTo((variableType.id << 4) + initialization.constant.typeID())
					: Constant.NotAConstant);
		}
		// Resolve Javadoc comment if one is present
		if (this.javadoc != null) {
			/*
			if (classScope != null) {
				this.javadoc.resolve(classScope);
			}
			*/
			if (scope.enclosingMethodScope()!=null)
				this.javadoc.resolve(scope.enclosingMethodScope());
			else
				this.javadoc.resolve(scope.compilationUnitScope());
		}  
		
		// only resolve annotation at the end, for constant to be positionned before (96991)
		if (JavaCore.IS_EMCASCRIPT4)
		resolveAnnotations(scope, this.annotations, this.binding);
	}
	public StringBuffer printStatement(int indent, StringBuffer output) {
		if (this.javadoc != null) {
			this.javadoc.print(indent, output);
		}
		return super.printStatement(indent, output);
	}
	
	public void traverse(ASTVisitor visitor, BlockScope scope) {

		if (visitor.visit(this, scope)) {
			if (this.annotations != null) {
				int annotationsLength = this.annotations.length;
				for (int i = 0; i < annotationsLength; i++)
					this.annotations[i].traverse(visitor, scope);
			}
			if (type!=null)
				type.traverse(visitor, scope);
			if (initialization != null)
				initialization.traverse(visitor, scope);
		}
		visitor.endVisit(this, scope);
	}
	
	public String getTypeName()
	{
		if (type!=null)
			return type.toString();
		if (inferredType!=null)
			return new String(inferredType.getName());
		return null;
	}
}
