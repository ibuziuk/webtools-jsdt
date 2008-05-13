/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.core.tests.dom;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.wst.jsdt.core.*;
import org.eclipse.wst.jsdt.core.compiler.IProblem;
import org.eclipse.wst.jsdt.core.dom.*;
import org.eclipse.wst.jsdt.core.tests.model.CancelCounter;
import org.eclipse.wst.jsdt.core.tests.model.Canceler;
import org.eclipse.wst.jsdt.core.tests.model.ReconcilerTests;
import org.eclipse.wst.jsdt.core.tests.util.Util;

public class ASTConverterTest2 extends ConverterTestSetup {
	
	/** @deprecated using deprecated code */
	public void setUpSuite() throws Exception {
		super.setUpSuite();
		this.ast = AST.newAST(AST.JLS2);
	}

	public ASTConverterTest2(String name) {
		super(name);
	}

	static {
//		TESTS_NAMES = new String[] {"test0578"};
//		TESTS_NUMBERS =  new int[] { 606 };
	}
	public static Test suite() {
		return buildModelTestSuite(ASTConverterTest2.class);
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=22560
	 * @deprecated using deprecated code
	 */
	public void test0401() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0401", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not a return statement", statement.getNodeType() == ASTNode.RETURN_STATEMENT); //$NON-NLS-1$
		ReturnStatement returnStatement = (ReturnStatement) statement;
		Expression expression = returnStatement.getExpression();
		assertNotNull("there is no expression", expression); //$NON-NLS-1$
		// call the default initialization
		methodDeclaration.getReturnType();
		ITypeBinding typeBinding = expression.resolveTypeBinding();
		assertNotNull("No typebinding", typeBinding); //$NON-NLS-1$
		assertEquals("wrong name", "int", typeBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}	
	
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23464
	 */
	public void test0402() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0402", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		ASTNode node = getASTNode((JavaScriptUnit) result, 1, 0, 0);
		assertEquals("Wrong number of problems", 0, ((JavaScriptUnit) result).getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not a super method invocation", node.getNodeType() == ASTNode.SUPER_CONSTRUCTOR_INVOCATION); //$NON-NLS-1$
		checkSourceRange(node, "new A().super();", source); //$NON-NLS-1$
	}	
	
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23597
	 */
	public void test0403() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0403", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		ASTNode node = getASTNode((JavaScriptUnit) result, 1, 0, 1);
		assertEquals("Wrong number of problems", 1, ((JavaScriptUnit) result).getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		assertTrue("Not a simple name", expression2.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) expression2;
		IBinding binding  = simpleName.resolveBinding();
		assertNotNull("No binding", binding); //$NON-NLS-1$
		assertTrue("wrong type", binding.getKind() == IBinding.VARIABLE); //$NON-NLS-1$
		IVariableBinding variableBinding = (IVariableBinding) binding;
		assertEquals("Wrong name", "test", variableBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		SimpleName simpleName2 = methodInvocation.getName();
		assertEquals("Wrong name", "clone", simpleName2.getIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
		IBinding binding2 = simpleName2.resolveBinding();
		assertNotNull("no binding2", binding2); //$NON-NLS-1$
		assertTrue("Wrong type", binding2.getKind() == IBinding.METHOD); //$NON-NLS-1$
		IFunctionBinding methodBinding = (IFunctionBinding) binding2;
		assertEquals("Wrong name", "clone", methodBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		IFunctionBinding methodBinding2 = methodInvocation.resolveMethodBinding();
		assertNotNull("No method binding2", methodBinding2);
		assertTrue("Wrong binding", methodBinding == methodBinding2);
	}	

	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23597
	 */
	public void test0404() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0404", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0, 1);
		assertEquals("Wrong number of problems", 1, ((JavaScriptUnit) result).getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		assertTrue("Not a simple name", expression2.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) expression2;
		IBinding binding  = simpleName.resolveBinding();
		assertNotNull("No binding", binding); //$NON-NLS-1$
		assertTrue("wrong type", binding.getKind() == IBinding.VARIABLE); //$NON-NLS-1$
		IVariableBinding variableBinding = (IVariableBinding) binding;
		assertEquals("Wrong name", "a", variableBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		SimpleName simpleName2 = methodInvocation.getName();
		assertEquals("Wrong name", "clone", simpleName2.getIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
		IBinding binding2 = simpleName2.resolveBinding();
		assertNotNull("no binding2", binding2); //$NON-NLS-1$
		assertTrue("Wrong type", binding2.getKind() == IBinding.METHOD); //$NON-NLS-1$
		IFunctionBinding methodBinding = (IFunctionBinding) binding2;
		assertEquals("Wrong name", "clone", methodBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}	

	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23597
	 */
	public void test0405() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0405", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		ASTNode node = getASTNode((JavaScriptUnit) result, 1, 0, 1);
		assertEquals("Wrong number of problems", 1, ((JavaScriptUnit) result).getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		assertTrue("Not a simple name", expression2.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) expression2;
		IBinding binding  = simpleName.resolveBinding();
		assertNotNull("No binding", binding); //$NON-NLS-1$
		assertTrue("wrong type", binding.getKind() == IBinding.VARIABLE); //$NON-NLS-1$
		IVariableBinding variableBinding = (IVariableBinding) binding;
		assertEquals("Wrong name", "a", variableBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		SimpleName simpleName2 = methodInvocation.getName();
		assertEquals("Wrong name", "clone", simpleName2.getIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
		IBinding binding2 = simpleName2.resolveBinding();
		assertNotNull("no binding2", binding2); //$NON-NLS-1$
		assertTrue("Wrong type", binding2.getKind() == IBinding.METHOD); //$NON-NLS-1$
		IFunctionBinding methodBinding = (IFunctionBinding) binding2;
		assertEquals("Wrong name", "clone", methodBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}	

	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23597
	 */
	public void test0406() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0406", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit unit = (JavaScriptUnit) result;
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0, 1);
		assertEquals("Wrong number of problems", 1, ((JavaScriptUnit) result).getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		assertTrue("Not a simple name", expression2.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) expression2;
		IBinding binding  = simpleName.resolveBinding();
		assertNotNull("No binding", binding); //$NON-NLS-1$
		assertTrue("wrong type", binding.getKind() == IBinding.VARIABLE); //$NON-NLS-1$
		IVariableBinding variableBinding = (IVariableBinding) binding;
		assertEquals("Wrong name", "a", variableBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		SimpleName simpleName2 = methodInvocation.getName();
		assertEquals("Wrong name", "foo", simpleName2.getIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
		IBinding binding2 = simpleName2.resolveBinding();
		assertNotNull("no binding2", binding2); //$NON-NLS-1$
		assertTrue("Wrong type", binding2.getKind() == IBinding.METHOD); //$NON-NLS-1$
		IFunctionBinding methodBinding = (IFunctionBinding) binding2;
		assertEquals("Wrong name", "foo", methodBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		assertNull("Got a declaring node in the unit", unit.findDeclaringNode(methodBinding));
	}	

	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23162
	 */
	public void test0407() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0407", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("Wrong number of problems", 0, ((JavaScriptUnit) result).getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		SimpleName simpleName = methodDeclaration.getName();
		IBinding binding = simpleName.resolveBinding();
		assertNotNull("No binding", binding); //$NON-NLS-1$
		assertTrue("Not a method binding", binding.getKind() == IBinding.METHOD); //$NON-NLS-1$
		IFunctionBinding methodBinding = (IFunctionBinding) binding;
		assertEquals("wrong name", "foo", methodBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		methodDeclaration.setName(methodDeclaration.getAST().newSimpleName("foo2")); //$NON-NLS-1$
		IFunctionBinding methodBinding2 = methodDeclaration.resolveBinding();
		assertNotNull("No methodbinding2", methodBinding2); //$NON-NLS-1$
		assertEquals("wrong name", "foo", methodBinding2.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		simpleName = methodDeclaration.getName();
		IBinding binding2 = simpleName.resolveBinding();
		assertNull("Got a binding2", binding2); //$NON-NLS-1$
		
		ASTNode astNode = unit.findDeclaringNode(methodBinding);
		assertNotNull("No declaring node", astNode);
		assertEquals("wrong declaring node", methodDeclaration, astNode);
	}
	
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23162
	 * @deprecated using deprecated code
	 */
	public void test0408() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0408", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("Wrong number of problems", 0, ((JavaScriptUnit) result).getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Type type = methodDeclaration.getReturnType();
		assertTrue("Not a simple type", type.isSimpleType()); //$NON-NLS-1$
		SimpleType simpleType = (SimpleType) type;
		Name name = simpleType.getName();
		assertTrue("Not a qualified name", name.isQualifiedName()); //$NON-NLS-1$
		QualifiedName qualifiedName = (QualifiedName) name;
		name = qualifiedName.getQualifier();
		assertTrue("Not a qualified name", name.isQualifiedName()); //$NON-NLS-1$
		qualifiedName = (QualifiedName) name;
		name = qualifiedName.getQualifier();
		assertTrue("Not a simple name", name.isSimpleName()); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) name;
		IBinding binding = simpleName.resolveBinding();
		assertNotNull("No binding", binding); //$NON-NLS-1$
		assertTrue("Not a package binding", binding.getKind() == IBinding.PACKAGE); //$NON-NLS-1$
		assertEquals("Wrong name", "java", binding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}
		
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=23162
	 */
	public void test0409() throws JavaScriptModelException {
		Hashtable options = JavaScriptCore.getOptions();
		Hashtable newOptions = JavaScriptCore.getOptions();
		try {
			newOptions.put(JavaScriptCore.COMPILER_SOURCE, JavaScriptCore.VERSION_1_4);
			JavaScriptCore.setOptions(newOptions);
			IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0409", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			ASTNode result = runConversion(sourceUnit, true);
			assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
			JavaScriptUnit compilationUnit = (JavaScriptUnit) result; 
			assertProblemsSize(compilationUnit, 0);
			BindingsCollectorVisitor bindingsCollectorVisitor = new BindingsCollectorVisitor();
			compilationUnit.accept(bindingsCollectorVisitor);
			assertEquals("wrong number", 3, bindingsCollectorVisitor.getUnresolvedNodesSet().size()); //$NON-NLS-1$
			Map bindingsMap = bindingsCollectorVisitor.getBindingsMap();
			assertEquals("wrong number", 205, bindingsMap.size()); //$NON-NLS-1$
			ASTNodesCollectorVisitor nodesCollector = new ASTNodesCollectorVisitor();
			compilationUnit.accept(nodesCollector);
			Set detachedNodes = nodesCollector.getDetachedAstNodes();
			int counter = 0;
			for (Iterator iterator = detachedNodes.iterator(); iterator.hasNext(); ) {
				ASTNode detachedNode = (ASTNode) iterator.next();
				counter++;
				IBinding binding = (IBinding) bindingsMap.get(detachedNode);
				assertNotNull(binding);
				switch(detachedNode.getNodeType()) {
					case ASTNode.ARRAY_ACCESS :
					case ASTNode.ARRAY_CREATION :
					case ASTNode.ARRAY_INITIALIZER :
					case ASTNode.ASSIGNMENT :
					case ASTNode.BOOLEAN_LITERAL :
					case ASTNode.CAST_EXPRESSION :
					case ASTNode.CHARACTER_LITERAL :
					case ASTNode.CLASS_INSTANCE_CREATION :
					case ASTNode.CONDITIONAL_EXPRESSION :
					case ASTNode.FIELD_ACCESS :
					case ASTNode.INFIX_EXPRESSION :
					case ASTNode.INSTANCEOF_EXPRESSION :
					case ASTNode.FUNCTION_INVOCATION :
					case ASTNode.NULL_LITERAL :
					case ASTNode.NUMBER_LITERAL :
					case ASTNode.POSTFIX_EXPRESSION :
					case ASTNode.PREFIX_EXPRESSION :
					case ASTNode.THIS_EXPRESSION :
					case ASTNode.TYPE_LITERAL :
					case ASTNode.VARIABLE_DECLARATION_EXPRESSION :
						ITypeBinding typeBinding = ((Expression) detachedNode).resolveTypeBinding();
						if (!binding.equals(typeBinding)) {
							System.out.println(detachedNode);
						}
						assertTrue("binding not equals", binding.equals(typeBinding)); //$NON-NLS-1$
						break;						
					case ASTNode.VARIABLE_DECLARATION_FRAGMENT :
						assertTrue("binding not equals", binding.equals(((VariableDeclarationFragment) detachedNode).resolveBinding())); //$NON-NLS-1$
						break;						
					case ASTNode.ANONYMOUS_CLASS_DECLARATION :
						assertTrue("binding not equals", binding.equals(((AnonymousClassDeclaration) detachedNode).resolveBinding())); //$NON-NLS-1$
						break;
					case ASTNode.QUALIFIED_NAME :
					case ASTNode.SIMPLE_NAME :
						IBinding newBinding = ((Name) detachedNode).resolveBinding();
						assertTrue("binding not equals", binding.equals(newBinding)); //$NON-NLS-1$
						break;
					case ASTNode.ARRAY_TYPE :
					case ASTNode.SIMPLE_TYPE :
					case ASTNode.PRIMITIVE_TYPE :
						assertTrue("binding not equals", binding.equals(((Type) detachedNode).resolveBinding())); //$NON-NLS-1$
						break;
					case ASTNode.CONSTRUCTOR_INVOCATION :
						assertTrue("binding not equals", binding.equals(((ConstructorInvocation) detachedNode).resolveConstructorBinding())); //$NON-NLS-1$
						break;
					case ASTNode.IMPORT_DECLARATION :
						assertTrue("binding not equals", binding.equals(((ImportDeclaration) detachedNode).resolveBinding())); //$NON-NLS-1$
						break;
					case ASTNode.FUNCTION_DECLARATION :
						assertTrue("binding not equals", binding.equals(((FunctionDeclaration) detachedNode).resolveBinding())); //$NON-NLS-1$
						break;
					case ASTNode.PACKAGE_DECLARATION :
						assertTrue("binding not equals", binding.equals(((PackageDeclaration) detachedNode).resolveBinding())); //$NON-NLS-1$
						break;
					case ASTNode.TYPE_DECLARATION :
						assertTrue("binding not equals", binding.equals(((TypeDeclaration) detachedNode).resolveBinding())); //$NON-NLS-1$
						break;
				}
			}
		} finally {
			JavaScriptCore.setOptions(options);
		}
	}

	/**
	 * Test for message on jdt-core-dev
	 */
	public void test0410() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0410", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("Wrong number of problems", 0, ((JavaScriptUnit) result).getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0, 0);
		assertNotNull(node);
		assertTrue("Not a return statement", node.getNodeType() == ASTNode.RETURN_STATEMENT); //$NON-NLS-1$
		Expression expression = ((ReturnStatement) node).getExpression();
		assertTrue("Not an infix expression", expression.getNodeType() == ASTNode.INFIX_EXPRESSION); //$NON-NLS-1$
		InfixExpression infixExpression = (InfixExpression) expression;
		List extendedOperands = infixExpression.extendedOperands();
		assertEquals("wrong size", 3, extendedOperands.size()); //$NON-NLS-1$
	}

	/**
	 * Test for message on jdt-core-dev
	 */
	public void test0411() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0411", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("Wrong number of problems", 0, ((JavaScriptUnit) result).getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0, 0);
		assertNotNull(node);
		assertTrue("Not a return statement", node.getNodeType() == ASTNode.RETURN_STATEMENT); //$NON-NLS-1$
		Expression expression = ((ReturnStatement) node).getExpression();
		assertTrue("Not an infix expression", expression.getNodeType() == ASTNode.INFIX_EXPRESSION); //$NON-NLS-1$
		InfixExpression infixExpression = (InfixExpression) expression;
		List extendedOperands = infixExpression.extendedOperands();
		assertEquals("wrong size", 0, extendedOperands.size()); //$NON-NLS-1$
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=23901
	 */
	public void test0412() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0412", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0);
		assertNotNull(node);
		assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION); //$NON-NLS-1$
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		assertTrue("Not an interface", typeDeclaration.isInterface()); //$NON-NLS-1$
		ITypeBinding typeBinding = typeDeclaration.resolveBinding();
		assertNotNull("No type binding", typeBinding); //$NON-NLS-1$
		assertNotNull("No declaring node", unit.findDeclaringNode(typeBinding)); //$NON-NLS-1$
		Name name = typeDeclaration.getName();
		IBinding binding = name.resolveBinding();
		assertNotNull("No binding", binding); //$NON-NLS-1$
		ASTNode declaringNode = unit.findDeclaringNode(binding);
		assertNotNull("No declaring node", declaringNode); //$NON-NLS-1$
		assertEquals("Wrong node", typeDeclaration, declaringNode); //$NON-NLS-1$
		typeBinding = name.resolveTypeBinding();
		assertNotNull("No type binding", typeBinding); //$NON-NLS-1$
		declaringNode = unit.findDeclaringNode(typeBinding);
		assertNotNull("No declaring node", declaringNode); //$NON-NLS-1$
		assertEquals("Wrong node", typeDeclaration, declaringNode); //$NON-NLS-1$
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=20881
	 */
	public void test0413() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0413", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 1, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		List throwsException = methodDeclaration.thrownExceptions();
		assertEquals("wrong size", 2, throwsException.size()); //$NON-NLS-1$
		Name name = (Name) throwsException.get(0);
		IBinding binding = name.resolveBinding();
		assertNull("Got a binding", binding); //$NON-NLS-1$
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=23734
	 * @deprecated using deprecated code
	 */
	public void test0414() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0414", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Type type = methodDeclaration.getReturnType();
		ITypeBinding typeBinding = type.resolveBinding();
		assertNotNull("No type binding", typeBinding); //$NON-NLS-1$
		ASTNode declaringNode = unit.findDeclaringNode(typeBinding);
		assertNull("Got a declaring node", declaringNode); //$NON-NLS-1$

		node = getASTNode(unit, 0, 1);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration2 = (FunctionDeclaration) node;
		Type type2 = methodDeclaration2.getReturnType();
		ITypeBinding typeBinding2 = type2.resolveBinding();
		assertNotNull("No type binding", typeBinding2); //$NON-NLS-1$
		ASTNode declaringNode2 = unit.findDeclaringNode(typeBinding2);
		assertNotNull("No declaring node", declaringNode2); //$NON-NLS-1$

		IJavaScriptUnit sourceUnit2 = getCompilationUnit("Converter" , "src", "test0414", "B.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		result = runConversion(sourceUnit2, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit2 = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit2.getProblems().length); //$NON-NLS-1$
		ASTNode declaringNode3 = unit2.findDeclaringNode(typeBinding);
		assertNull("Got a declaring node", declaringNode3); //$NON-NLS-1$
		
		ASTNode declaringNode4 = unit2.findDeclaringNode(typeBinding.getKey());
		assertNotNull("No declaring node", declaringNode4); //$NON-NLS-1$
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24268
	 */
	public void test0415() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0415", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a switch statement", node.getNodeType() == ASTNode.SWITCH_STATEMENT); //$NON-NLS-1$
		SwitchStatement switchStatement = (SwitchStatement) node;
		List statements = switchStatement.statements();
		assertEquals("wrong size", statements.size(), 5); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(3);
		assertTrue("not a switch case (default)", statement.getNodeType() == ASTNode.SWITCH_CASE); //$NON-NLS-1$
		SwitchCase defaultCase = (SwitchCase) statement;
		assertTrue("not a default case", defaultCase.isDefault());
		checkSourceRange(defaultCase, "default:", source);
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24324
	 */
	public void test0416() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0416", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a variable declaration statement", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		List fragments = statement.fragments();
		assertEquals("Wrong size", fragments.size(), 1);
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression init = fragment.getInitializer();
		assertTrue("not a qualified name", init.getNodeType() == ASTNode.QUALIFIED_NAME); //$NON-NLS-1$
		QualifiedName qualifiedName = (QualifiedName) init;
		SimpleName simpleName = qualifiedName.getName();
		assertEquals("Wrong name", "CONST", simpleName.getIdentifier());
		IBinding binding = simpleName.resolveBinding();
		assertEquals("Wrong type", IBinding.VARIABLE, binding.getKind());
		IVariableBinding variableBinding = (IVariableBinding) binding;
		assertEquals("Wrong modifier", variableBinding.getModifiers(), Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
		ASTNode declaringNode = unit.findDeclaringNode(variableBinding);
		assertNotNull("No declaring node", declaringNode);
		assertTrue("not a variable declaration fragment", declaringNode.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT);
		VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment) declaringNode;
		FieldDeclaration fieldDeclaration = (FieldDeclaration) variableDeclarationFragment.getParent();
		assertEquals("Wrong modifier", fieldDeclaration.getModifiers(), Modifier.NONE);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24347
	 */
	public void test0417() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0417", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a variable declaration statement", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		Type type = statement.getType();
		assertTrue("not a simple type", type.getNodeType() == ASTNode.SIMPLE_TYPE); //$NON-NLS-1$
		SimpleType simpleType = (SimpleType) type;
		Name name = simpleType.getName();
		assertTrue("Not a qualified name", name.isQualifiedName());
		QualifiedName qualifiedName = (QualifiedName) name;
		Name qualifier = qualifiedName.getQualifier();
		assertTrue("Not a simple name", qualifier.isSimpleName());
		IBinding binding = qualifier.resolveBinding();
		assertNotNull("No binding", binding);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24406
	 */
	public void test0418() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0418", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 1, 0);
		assertNotNull("No node", node);
		assertTrue("not an expression statement ", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertTrue("not an method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Name name = methodInvocation.getName();
		IBinding binding = name.resolveBinding();
		assertNotNull("No binding", binding);
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24449
	 */
	public void test0419() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0419", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("Not an expression statement", node.getNodeType(), ASTNode.EXPRESSION_STATEMENT);
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertEquals("Not an assignment", expression.getNodeType(), ASTNode.ASSIGNMENT);
		Assignment assignment = (Assignment) expression;
		Expression expression2 = assignment.getLeftHandSide();
		assertEquals("Not a name", expression2.getNodeType(), ASTNode.SIMPLE_NAME);
		SimpleName simpleName = (SimpleName) expression2;
		IBinding binding = simpleName.resolveBinding();
		assertNull(binding);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24453
	 */
	public void test0420() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0420", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a variable declaration statement", node.getNodeType(), ASTNode.VARIABLE_DECLARATION_STATEMENT);
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Not an infix expression", expression.getNodeType(), ASTNode.INFIX_EXPRESSION);
		InfixExpression infixExpression = (InfixExpression) expression;
		Expression expression2 = infixExpression.getRightOperand();
		assertEquals("Not a parenthesized expression", expression2.getNodeType(), ASTNode.PARENTHESIZED_EXPRESSION);
		checkSourceRange(expression2, "(2 + 3)", source);
		ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression2;
		Expression expression3 = parenthesizedExpression.getExpression();
		checkSourceRange(expression3, "2 + 3", source);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24453
	 */
	public void test0421() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0421", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a variable declaration statement", node.getNodeType(), ASTNode.VARIABLE_DECLARATION_STATEMENT);
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Not an infix expression", expression.getNodeType(), ASTNode.INFIX_EXPRESSION);
		InfixExpression infixExpression = (InfixExpression) expression;
		checkSourceRange(infixExpression, "(1 + 2) + 3", source);
		Expression expression2 = infixExpression.getLeftOperand();
		assertEquals("Not a parenthesized expression", expression2.getNodeType(), ASTNode.PARENTHESIZED_EXPRESSION);
		checkSourceRange(expression2, "(1 + 2)", source);
		ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression2;
		Expression expression3 = parenthesizedExpression.getExpression();
		checkSourceRange(expression3, "1 + 2", source);
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24453
	 */
	public void test0422() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0422", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a variable declaration statement", node.getNodeType(), ASTNode.VARIABLE_DECLARATION_STATEMENT);
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Not an infix expression", expression.getNodeType(), ASTNode.INFIX_EXPRESSION);
		InfixExpression infixExpression = (InfixExpression) expression;
		checkSourceRange(infixExpression, "( 1 + 2 ) + 3", source);
		Expression expression2 = infixExpression.getLeftOperand();
		assertEquals("Not a parenthesized expression", expression2.getNodeType(), ASTNode.PARENTHESIZED_EXPRESSION);
		checkSourceRange(expression2, "( 1 + 2 )", source);
		ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression2;
		Expression expression3 = parenthesizedExpression.getExpression();
		checkSourceRange(expression3, "1 + 2", source);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24453
	 */
	public void test0423() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0423", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a variable declaration statement", node.getNodeType(), ASTNode.VARIABLE_DECLARATION_STATEMENT);
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Not an infix expression", expression.getNodeType(), ASTNode.INFIX_EXPRESSION);
		InfixExpression infixExpression = (InfixExpression) expression;
		Expression expression2 = infixExpression.getRightOperand();
		assertEquals("Not a parenthesized expression", expression2.getNodeType(), ASTNode.PARENTHESIZED_EXPRESSION);
		checkSourceRange(expression2, "( 2 + 3 )", source);
		ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression2;
		Expression expression3 = parenthesizedExpression.getExpression();
		checkSourceRange(expression3, "2 + 3", source);
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24453
	 */
	public void test0424() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0424", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a variable declaration statement", node.getNodeType(), ASTNode.VARIABLE_DECLARATION_STATEMENT);
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Not an infix expression", expression.getNodeType(), ASTNode.INFIX_EXPRESSION);
		InfixExpression infixExpression = (InfixExpression) expression;
		assertEquals("Wrong size", 1, infixExpression.extendedOperands().size());
		Expression expression2 = (Expression) infixExpression.extendedOperands().get(0);
		checkSourceRange(expression2, "( 2 + 3 )", source);
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24453
	 */
	public void test0425() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0425", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a variable declaration statement", node.getNodeType(), ASTNode.VARIABLE_DECLARATION_STATEMENT);
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Not an infix expression", expression.getNodeType(), ASTNode.INFIX_EXPRESSION);
		InfixExpression infixExpression = (InfixExpression) expression;
		assertEquals("Wrong size", 0, infixExpression.extendedOperands().size());
		Expression expression2 = infixExpression.getRightOperand();
		assertTrue("not an infix expression", expression2.getNodeType() == ASTNode.INFIX_EXPRESSION); //$NON-NLS-1$
		InfixExpression infixExpression2 = (InfixExpression) expression2;
		Expression expression3 = infixExpression2.getRightOperand();
		assertTrue("not a parenthesized expression", expression3.getNodeType() == ASTNode.PARENTHESIZED_EXPRESSION); //$NON-NLS-1$
		checkSourceRange(expression3, "( 2 + 3 )", source);
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24449
	 */
	public void test0426() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0426", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("Not a variable declaration statement", node.getNodeType(), ASTNode.VARIABLE_DECLARATION_STATEMENT);
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		Type type = statement.getType();
		assertTrue("not a simple type", type.getNodeType() == ASTNode.SIMPLE_TYPE);
		SimpleType simpleType = (SimpleType) type;
		Name name = simpleType.getName();
		assertNotNull("No name", name);
		IBinding binding = name.resolveBinding();
		assertNotNull("No binding", binding);
	}	
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24449
	 */
	public void test0427() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0427", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("Not an expression statement", node.getNodeType(), ASTNode.EXPRESSION_STATEMENT);
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertEquals("Not an assignment", expression.getNodeType(), ASTNode.ASSIGNMENT);
		Assignment assignment = (Assignment) expression;
		Expression expression2 = assignment.getLeftHandSide();
		assertEquals("Not a super field access", expression2.getNodeType(), ASTNode.SUPER_FIELD_ACCESS);
		SuperFieldAccess superFieldAccess = (SuperFieldAccess) expression2;
		Name name = superFieldAccess.getName();
		assertNotNull("No name", name);
		IBinding binding = name.resolveBinding();
		assertNull("Got a binding", binding);
		assertNull("Got a binding", superFieldAccess.resolveFieldBinding());
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24449
	 */
	public void test0428() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0428", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("Not an expression statement", node.getNodeType(), ASTNode.EXPRESSION_STATEMENT);
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertEquals("Not an assignment", expression.getNodeType(), ASTNode.ASSIGNMENT);
		Assignment assignment = (Assignment) expression;
		Expression expression2 = assignment.getLeftHandSide();
		assertEquals("Not a qualified name", expression2.getNodeType(), ASTNode.QUALIFIED_NAME);
		QualifiedName name = (QualifiedName) expression2;
		SimpleName simpleName = name.getName();
		IBinding binding = simpleName.resolveBinding();
		assertNotNull("No binding", binding);
		IBinding binding2 = name.resolveBinding();
		assertNotNull("No binding2", binding2);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24449
	 */
	public void test0429() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0429", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("Not an expression statement", node.getNodeType(), ASTNode.EXPRESSION_STATEMENT);
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertEquals("Not an assignment", expression.getNodeType(), ASTNode.ASSIGNMENT);
		Assignment assignment = (Assignment) expression;
		Expression expression2 = assignment.getLeftHandSide();
		assertEquals("Not a qualified name", expression2.getNodeType(), ASTNode.QUALIFIED_NAME);
		QualifiedName name = (QualifiedName) expression2;
		SimpleName simpleName = name.getName();
		IBinding binding = simpleName.resolveBinding();
		assertNotNull("No binding", binding);
		IBinding binding2 = name.resolveBinding();
		assertNotNull("No binding2", binding2);
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24499
	 */
	public void test0430() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0430", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertTrue("Not a constructor invocation", node.getNodeType() == ASTNode.CONSTRUCTOR_INVOCATION);
		ConstructorInvocation constructorInvocation = (ConstructorInvocation) node;
		checkSourceRange(constructorInvocation, "this(coo2());", source);
		List arguments = constructorInvocation.arguments();
		assertEquals("Wrong size", 1, arguments.size());
		Expression expression = (Expression) arguments.get(0);
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION);
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		SimpleName simpleName = methodInvocation.getName();
		IBinding binding = simpleName.resolveBinding();
		assertNotNull("No binding", binding);
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24500
	 */
	public void test0431() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0431", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertTrue("Not a constructor invocation", node.getNodeType() == ASTNode.CONSTRUCTOR_INVOCATION);
		ConstructorInvocation constructorInvocation = (ConstructorInvocation) node;
		List arguments = constructorInvocation.arguments();
		assertEquals("Wrong size", 1, arguments.size());
		Expression expression = (Expression) arguments.get(0);
		assertTrue("Not a simple name", expression.getNodeType() == ASTNode.SIMPLE_NAME);
		SimpleName simpleName = (SimpleName) expression;
		IBinding binding = simpleName.resolveBinding();
		assertNotNull("No binding", binding);
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24501
	 */
	public void test0432() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0432", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("Not an expression statement", ASTNode.EXPRESSION_STATEMENT, node.getNodeType());
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertEquals("Not an assignment", ASTNode.ASSIGNMENT, expression.getNodeType());
		Assignment assignment = (Assignment) expression;
		Expression expression2 = assignment.getLeftHandSide();
		assertEquals("Not a simple name", ASTNode.SIMPLE_NAME, expression2.getNodeType());
		SimpleName simpleName = (SimpleName) expression2;
		IBinding binding = simpleName.resolveBinding();
		assertNotNull("No binding", binding);
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24501
	 */
	public void test0433() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0433", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("Not an expression statement", ASTNode.EXPRESSION_STATEMENT, node.getNodeType());
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertEquals("Not an assignment", ASTNode.ASSIGNMENT, expression.getNodeType());
		Assignment assignment = (Assignment) expression;
		Expression expression2 = assignment.getLeftHandSide();
		assertEquals("Not a super field access", ASTNode.SUPER_FIELD_ACCESS, expression2.getNodeType());
		SuperFieldAccess superFieldAccess = (SuperFieldAccess) expression2;
		SimpleName simpleName = superFieldAccess.getName();
		assertEquals("wrong name", "fCoo", simpleName.getIdentifier());
		IBinding binding = simpleName.resolveBinding();
		assertNotNull("No binding", binding);
		assertEquals("Wrong binding", IBinding.VARIABLE, binding.getKind());
		IVariableBinding variableBinding = superFieldAccess.resolveFieldBinding();
		assertTrue("Different binding", binding == variableBinding);
		ASTNode astNode = unit.findDeclaringNode(variableBinding);
		assertTrue("Wrong type", astNode.getNodeType() == ASTNode.SINGLE_VARIABLE_DECLARATION || astNode.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT || astNode.getNodeType() == ASTNode.VARIABLE_DECLARATION_EXPRESSION);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24501
	 */
	public void test0434() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0434", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("Not an expression statement", ASTNode.EXPRESSION_STATEMENT, node.getNodeType());
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertEquals("Not an assignment", ASTNode.ASSIGNMENT, expression.getNodeType());
		Assignment assignment = (Assignment) expression;
		Expression expression2 = assignment.getLeftHandSide();
		assertEquals("Not a qualified name", ASTNode.QUALIFIED_NAME, expression2.getNodeType());
		QualifiedName qualifiedName = (QualifiedName) expression2;
		SimpleName simpleName = qualifiedName.getName();
		assertEquals("wrong name", "fCoo", simpleName.getIdentifier());
		IBinding binding = simpleName.resolveBinding();
		assertNotNull("No binding", binding);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24501
	 */
	public void test0435() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0435", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("Not an expression statement", ASTNode.EXPRESSION_STATEMENT, node.getNodeType());
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertEquals("Not an assignment", ASTNode.ASSIGNMENT, expression.getNodeType());
		Assignment assignment = (Assignment) expression;
		Expression expression2 = assignment.getLeftHandSide();
		assertEquals("Not a qualified name", ASTNode.QUALIFIED_NAME, expression2.getNodeType());
		QualifiedName qualifiedName = (QualifiedName) expression2;
		SimpleName simpleName = qualifiedName.getName();
		assertEquals("wrong name", "fCoo", simpleName.getIdentifier());
		IBinding binding = simpleName.resolveBinding();
		assertNotNull("No binding", binding);
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24502
	 */
	public void test0436() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0436", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertProblemsSize(unit, 1, "The type A.CInner is not visible"); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("Not a variable declaration statement", ASTNode.VARIABLE_DECLARATION_STATEMENT, node.getNodeType());
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		Type type = statement.getType();
		assertEquals("Not a simple type", ASTNode.SIMPLE_TYPE, type.getNodeType());
		SimpleType simpleType = (SimpleType) type;
		Name name = simpleType.getName();
		IBinding binding = name.resolveBinding();
		assertNotNull("No binding", binding);
		assertEquals("Not a qualified name", ASTNode.QUALIFIED_NAME, name.getNodeType());
		QualifiedName qualifiedName = (QualifiedName) name;
		SimpleName simpleName = qualifiedName.getName();
		assertEquals("wrong name", "CInner", simpleName.getIdentifier());
		IBinding binding2 = simpleName.resolveBinding();
		assertNotNull("No binding", binding2);
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24502
	 */
	public void test0437() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0437", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertProblemsSize(unit, 1, "The type CInner is not visible"); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("Not a variable declaration statement", ASTNode.VARIABLE_DECLARATION_STATEMENT, node.getNodeType());
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		Type type = statement.getType();
		assertEquals("Not a simple type", ASTNode.SIMPLE_TYPE, type.getNodeType());
		SimpleType simpleType = (SimpleType) type;
		Name name = simpleType.getName();
		assertEquals("Not a simple name", ASTNode.SIMPLE_NAME, name.getNodeType());
		SimpleName simpleName = (SimpleName) name;
		IBinding binding = simpleName.resolveBinding();
		assertNotNull("No binding", binding);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24511
	 */
	public void test0438() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0438", "D.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		List imports = unit.imports();
		assertEquals("Wrong size", 1, imports.size()); //$NON-NLS-1$<
		ImportDeclaration importDeclaration = (ImportDeclaration) imports.get(0);
		IBinding binding = importDeclaration.resolveBinding();
		assertNotNull("No binding", binding);
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24502
	 */
	public void test0439() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0439", "E.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a variable declaration statement", ASTNode.VARIABLE_DECLARATION_STATEMENT, node.getNodeType());
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		Type type = statement.getType();
		assertEquals("Not a simple type", ASTNode.SIMPLE_TYPE, type.getNodeType());
		SimpleType simpleType = (SimpleType) type;
		Name name = simpleType.getName();
		IBinding binding = name.resolveBinding();
		assertNotNull("No binding", binding);
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24622
	 */
	public void test0440() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0440", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a variable declaration statement", ASTNode.VARIABLE_DECLARATION_STATEMENT, node.getNodeType());
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		List fragments = statement.fragments();
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Not an infix expression", ASTNode.INFIX_EXPRESSION, expression.getNodeType());
		// 2 * 3 + "" + (true)
		InfixExpression infixExpression = (InfixExpression) expression;
		checkSourceRange(infixExpression, "2 * 3 + \"\" + (true)", source);
		Expression leftOperand = infixExpression.getLeftOperand();
		checkSourceRange(leftOperand, "2 * 3 + \"\"", source);
		checkSourceRange(infixExpression.getRightOperand(), "(true)", source);
		assertEquals("wrong operator", infixExpression.getOperator(), InfixExpression.Operator.PLUS);
		assertEquals("wrong type", ASTNode.INFIX_EXPRESSION, leftOperand.getNodeType());
		infixExpression = (InfixExpression) leftOperand;
		checkSourceRange(infixExpression, "2 * 3 + \"\"", source);
		leftOperand = infixExpression.getLeftOperand();
		checkSourceRange(leftOperand, "2 * 3", source);
		checkSourceRange(infixExpression.getRightOperand(), "\"\"", source);
		assertEquals("wrong operator", infixExpression.getOperator(), InfixExpression.Operator.PLUS);
		assertEquals("wrong type", ASTNode.INFIX_EXPRESSION, leftOperand.getNodeType());
		infixExpression = (InfixExpression) leftOperand;
		checkSourceRange(infixExpression, "2 * 3", source);
		leftOperand = infixExpression.getLeftOperand();
		checkSourceRange(leftOperand, "2", source);
		checkSourceRange(infixExpression.getRightOperand(), "3", source);
		assertEquals("wrong operator", infixExpression.getOperator(), InfixExpression.Operator.TIMES);
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24622
	 */
	public void test0441() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0441", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a variable declaration statement", ASTNode.VARIABLE_DECLARATION_STATEMENT, node.getNodeType());
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		List fragments = statement.fragments();
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Not an infix expression", ASTNode.INFIX_EXPRESSION, expression.getNodeType());
		InfixExpression infixExpression = (InfixExpression) expression;
		checkSourceRange(infixExpression, "(2 + 2) * 3 * 1", source);
		Expression leftOperand = infixExpression.getLeftOperand();
		checkSourceRange(leftOperand, "(2 + 2)", source);
		checkSourceRange(infixExpression.getRightOperand(), "3", source);
		List extendedOperands = infixExpression.extendedOperands();
		assertEquals("wrong size", 1, extendedOperands.size());
		checkSourceRange((Expression) extendedOperands.get(0), "1", source);
		assertEquals("wrong operator", InfixExpression.Operator.TIMES, infixExpression.getOperator());
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24622
	 */
	public void test0442() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0442", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a variable declaration statement", ASTNode.VARIABLE_DECLARATION_STATEMENT, node.getNodeType());
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		List fragments = statement.fragments();
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Not an infix expression", ASTNode.INFIX_EXPRESSION, expression.getNodeType());
		InfixExpression infixExpression = (InfixExpression) expression;
		checkSourceRange(infixExpression, "2 + (2 * 3) + 1", source);
		Expression leftOperand = infixExpression.getLeftOperand();
		checkSourceRange(leftOperand, "2", source);
		Expression rightOperand = infixExpression.getRightOperand();
		checkSourceRange(rightOperand, "(2 * 3)", source);
		assertEquals("wrong type", ASTNode.PARENTHESIZED_EXPRESSION, rightOperand.getNodeType());
		List extendedOperands = infixExpression.extendedOperands();
		assertEquals("wrong size", 1, extendedOperands.size());
		checkSourceRange((Expression) extendedOperands.get(0), "1", source);
		assertEquals("wrong operator", InfixExpression.Operator.PLUS, infixExpression.getOperator());
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24623
	 */
	public void test0443() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0443", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 2, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0);
		assertEquals("Wrong type", ASTNode.FUNCTION_DECLARATION, node.getNodeType());
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertNotNull("No body", methodDeclaration.getBody());
		assertNotNull("No binding", methodDeclaration.resolveBinding());
		assertTrue("Not an abstract method", Modifier.isAbstract(methodDeclaration.getModifiers()));
		assertTrue("Not malformed", isMalformed(methodDeclaration)); 
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24623
	 */
	public void test0444() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0444", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 2, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0);
		assertEquals("Wrong type", ASTNode.TYPE_DECLARATION, node.getNodeType());
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		List bodyDeclarations = typeDeclaration.bodyDeclarations();
		assertEquals("Wrong size", 2, bodyDeclarations.size());
		BodyDeclaration bodyDeclaration = (BodyDeclaration)bodyDeclarations.get(0);
		assertEquals("Wrong type", ASTNode.FUNCTION_DECLARATION, bodyDeclaration.getNodeType());
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) bodyDeclaration;
		assertEquals("Wrong name", "foo", methodDeclaration.getName().getIdentifier());
		assertNull("Got a binding", methodDeclaration.resolveBinding());
		bodyDeclaration = (BodyDeclaration)bodyDeclarations.get(1);
		assertEquals("Wrong type", ASTNode.FUNCTION_DECLARATION, bodyDeclaration.getNodeType());
		assertEquals("Wrong name", "foo", ((FunctionDeclaration) bodyDeclaration).getName().getIdentifier());
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24773
	 */
	public void test0445() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0445", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=25018
	 */
	public void test0446() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0446", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 2, unit.getProblems().length); //$NON-NLS-1$<
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=25124
	 */
	public void test0447() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0447", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 3, unit.getProblems().length); //$NON-NLS-1$<
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=25330
	 * @deprecated using deprecated code
	 */
	public void test0448() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0448", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0);
		assertEquals("Not a method declaration", node.getNodeType(), ASTNode.FUNCTION_DECLARATION);
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Not a constructor", methodDeclaration.isConstructor());
		ITypeBinding returnTypeBinding = methodDeclaration.getReturnType().resolveBinding();
		assertNotNull("No return type binding", returnTypeBinding);
		Block block = methodDeclaration.getBody();
		assertNotNull("No method body", block);
		assertEquals("wrong size", 0, block.statements().size()); 
	}
	
	/**
	 * Check that the implicit super constructor call is not there
	 */
	public void test0449() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0449", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0);
		assertEquals("Not a method declaration", node.getNodeType(), ASTNode.FUNCTION_DECLARATION);
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Not a constructor", methodDeclaration.isConstructor());
		Block block = methodDeclaration.getBody();
		assertNotNull("No method body", block);
		assertEquals("wrong size", 1, block.statements().size()); 
	}	
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=26452
	 */
	public void test0450() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0450", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0);
		assertEquals("Not a type declaration", node.getNodeType(), ASTNode.TYPE_DECLARATION);
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		ITypeBinding typeBinding = typeDeclaration.resolveBinding();
		assertNotNull("No type binding", typeBinding);
		assertTrue("not a class", typeBinding.isClass());
		assertTrue("not a toplevel type", typeBinding.isTopLevel());
		assertTrue("a local type", !typeBinding.isLocal());
		assertTrue("an anonymous type", !typeBinding.isAnonymous());
		assertTrue("a member type", !typeBinding.isMember());
		assertTrue("a nested type", !typeBinding.isNested());
		node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not an expression statement", node.getNodeType(), ASTNode.EXPRESSION_STATEMENT);
		Expression expression = ((ExpressionStatement) node).getExpression();
		assertEquals("Not a class instance creation", expression.getNodeType(), ASTNode.CLASS_INSTANCE_CREATION);
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
		AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
		typeBinding = anonymousClassDeclaration.resolveBinding();
		assertNotNull("No type binding", typeBinding);
		assertTrue("not a class", typeBinding.isClass());
		assertTrue("a toplevel type", !typeBinding.isTopLevel());
		assertTrue("not a local type", typeBinding.isLocal());
		assertTrue("not an anonymous type", typeBinding.isAnonymous());
		assertTrue("a member type", !typeBinding.isMember());
		assertTrue("not a nested type", typeBinding.isNested());
		ASTNode astNode = unit.findDeclaringNode(typeBinding);
		assertEquals("Wrong type", ASTNode.ANONYMOUS_CLASS_DECLARATION, astNode.getNodeType());
		assertNotNull("Didn't get a key", typeBinding.getKey());
		astNode = unit.findDeclaringNode(typeBinding.getKey());
		assertNotNull("Didn't get a declaring node", astNode);
		
		ITypeBinding typeBinding3 = classInstanceCreation.resolveTypeBinding();
		assertEquals("wrong binding", typeBinding, typeBinding3);
		
		List bodyDeclarations = anonymousClassDeclaration.bodyDeclarations();
		assertEquals("wrong size", 2, bodyDeclarations.size());
		BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
		assertTrue("not a type declaration", bodyDeclaration.getNodeType() == ASTNode.TYPE_DECLARATION);
		typeDeclaration = (TypeDeclaration) bodyDeclaration;
		
		bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(1);
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) bodyDeclaration;
		Block block = methodDeclaration.getBody();
		assertNotNull("No body", block);
		List statements = block.statements();
		assertEquals("wrong size", 2, statements.size());
		Statement statement = (Statement) statements.get(1);
		assertEquals("Not a variable declaration statement", statement.getNodeType(), ASTNode.VARIABLE_DECLARATION_STATEMENT);
		VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) statement;
		Type type = variableDeclarationStatement.getType();
		assertNotNull("No type", type);
		
		ITypeBinding typeBinding2 = type.resolveBinding();
		typeBinding = typeDeclaration.resolveBinding();
		assertTrue("not equals", typeBinding == typeBinding2);
		assertNotNull("No type binding", typeBinding);
		assertTrue("not a class", typeBinding.isClass());
		assertTrue("a toplevel type", !typeBinding.isTopLevel());
		assertTrue("an anonymous type", !typeBinding.isAnonymous());
		assertTrue("not a member type", typeBinding.isMember());
		assertTrue("not a nested type", typeBinding.isNested());		
		assertTrue("a local type", !typeBinding.isLocal());
		
		bodyDeclarations = typeDeclaration.bodyDeclarations();
		assertEquals("wrong size", 1, bodyDeclarations.size());
		bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
		assertTrue("not a type declaration", bodyDeclaration.getNodeType() == ASTNode.TYPE_DECLARATION);
		typeDeclaration = (TypeDeclaration) bodyDeclaration;
		typeBinding = typeDeclaration.resolveBinding();
		assertNotNull("No type binding", typeBinding);
		assertTrue("not a class", typeBinding.isClass());
		assertTrue("a toplevel type", !typeBinding.isTopLevel());
		assertTrue("an anonymous type", !typeBinding.isAnonymous());
		assertTrue("not a member type", typeBinding.isMember());
		assertTrue("not a nested type", typeBinding.isNested());		
		assertTrue("a local type", !typeBinding.isLocal());
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=24916
	 * @deprecated using deprecated code
	 */
	public void test0451() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0451", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 2, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Type type = methodDeclaration.getReturnType();
		checkSourceRange(type, "int", source);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=27204
	 */
	public void test0452() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "", "NO_WORKING.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, false);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		SimpleName name = methodDeclaration.getName();
		assertEquals("wrong line number", 3, compilationUnit.getLineNumber(name.getStartPosition()));
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=27173
	 */
	public void test0453() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0453", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 0,0);
		assertNotNull("No node", node);
		assertTrue("not a return statement", node.getNodeType() == ASTNode.RETURN_STATEMENT); //$NON-NLS-1$
		ReturnStatement returnStatement = (ReturnStatement) node;
		Expression expression = returnStatement.getExpression();
		assertTrue("not a super method invocation", expression.getNodeType() == ASTNode.SUPER_METHOD_INVOCATION); //$NON-NLS-1$
		SuperMethodInvocation methodInvocation = (SuperMethodInvocation) expression;
		IFunctionBinding methodBinding = methodInvocation.resolveMethodBinding();
		assertNotNull("No method binding", methodBinding);
		assertEquals("Wrong binding", "toString", methodBinding.getName());
	}	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=28296
	 */
	public void test0454() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0454", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 0,1);
		assertNotNull("No node", node);
		assertTrue("not a variable declaration statement", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node;
		List fragments = statement.fragments();
		assertEquals("wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertTrue("not a cast expression", expression.getNodeType() == ASTNode.CAST_EXPRESSION); //$NON-NLS-1$
		checkSourceRange(expression, "(int) (3.14f * a)", source);
		CastExpression castExpression = (CastExpression) expression;
		checkSourceRange(castExpression.getType(), "int", source);
		Expression expression2 = castExpression.getExpression();
		checkSourceRange(expression2, "(3.14f * a)", source);	
		assertTrue("not a parenthesized expression", expression2.getNodeType() == ASTNode.PARENTHESIZED_EXPRESSION); //$NON-NLS-1$
	}
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=28682
	 */
	public void test0455() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0455", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT); //$NON-NLS-1$
		ForStatement forStatement = (ForStatement) node; // first for loop
		String expectedSource = "for (int i = 0; i < 10; i++)  // for 1\n" +
			"	        for (int j = 0; j < 10; j++)  // for 2\n" +
			"	            if (true) { }";
		checkSourceRange(forStatement, expectedSource, source);
		Statement body = forStatement.getBody();
		expectedSource = "for (int j = 0; j < 10; j++)  // for 2\n" +
			"	            if (true) { }";
		checkSourceRange(body, expectedSource, source);		
		assertTrue("not a for statement", body.getNodeType() == ASTNode.FOR_STATEMENT); //$NON-NLS-1$
		ForStatement forStatement2 = (ForStatement) body;
		body = forStatement2.getBody();
		expectedSource = "if (true) { }";
		checkSourceRange(body, expectedSource, source);		
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=28682
	 */
	public void test0456() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0456", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT); //$NON-NLS-1$
		ForStatement forStatement = (ForStatement) node; // first for loop
		String expectedSource = "for (int x= 10; x < 20; x++)\n" +
			"			main();";
		checkSourceRange(forStatement, expectedSource, source);
		Statement body = forStatement.getBody();
		expectedSource = "main();";
		checkSourceRange(body, expectedSource, source);		
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=28682
	 */
	public void test0457() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0457", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT); //$NON-NLS-1$
		ForStatement forStatement = (ForStatement) node; // first for loop
		String expectedSource = "for (int i= 10; i < 10; i++)/*[*/\n"+
			"			for (int z= 10; z < 10; z++)\n" +
			"				foo();";
		checkSourceRange(forStatement, expectedSource, source);
		Statement body = forStatement.getBody();
		expectedSource = "for (int z= 10; z < 10; z++)\n" +
			"				foo();";
		checkSourceRange(body, expectedSource, source);		
		assertTrue("not a for statement", body.getNodeType() == ASTNode.FOR_STATEMENT); //$NON-NLS-1$
		ForStatement forStatement2 = (ForStatement) body;
		body = forStatement2.getBody();
		expectedSource = "foo();";
		checkSourceRange(body, expectedSource, source);		
	}	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=28682
	 */
	public void test0458() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0458", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT); //$NON-NLS-1$
		ForStatement forStatement = (ForStatement) node; // first for loop
		String expectedSource = "for (int i= 10; i < 10; i++)/*[*/\n"+
			"			for (int z= 10; z < 10; z++)\n" +
			"				;";
		checkSourceRange(forStatement, expectedSource, source);
		Statement body = forStatement.getBody();
		expectedSource = "for (int z= 10; z < 10; z++)\n" +
			"				;";
		checkSourceRange(body, expectedSource, source);		
		assertTrue("not a for statement", body.getNodeType() == ASTNode.FOR_STATEMENT); //$NON-NLS-1$
		ForStatement forStatement2 = (ForStatement) body;
		body = forStatement2.getBody();
		expectedSource = ";";
		checkSourceRange(body, expectedSource, source);		
		assertTrue("not an empty statement", body.getNodeType() == ASTNode.EMPTY_STATEMENT); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=28682
	 */
	public void test0459() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0459", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT); //$NON-NLS-1$
		ForStatement forStatement = (ForStatement) node; // first for loop
		String expectedSource = "for (int i= 10; i < 10; i++)/*[*/\n"+
			"			for (int z= 10; z < 10; z++)\n" +
			"				{    }";
		checkSourceRange(forStatement, expectedSource, source);
		Statement body = forStatement.getBody();
		expectedSource = "for (int z= 10; z < 10; z++)\n" +
			"				{    }";
		checkSourceRange(body, expectedSource, source);		
		assertTrue("not a for statement", body.getNodeType() == ASTNode.FOR_STATEMENT); //$NON-NLS-1$
		ForStatement forStatement2 = (ForStatement) body;
		body = forStatement2.getBody();
		expectedSource = "{    }";
		checkSourceRange(body, expectedSource, source);		
		assertTrue("not a block", body.getNodeType() == ASTNode.BLOCK); //$NON-NLS-1$
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=28869
	 */
	public void test0460() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0460", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		assertTrue("Has error", compilationUnit.getProblems().length == 0); //$NON-NLS-1$
		ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("Malformed", !isMalformed(node));
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=28824
	 */
	public void test0461() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0461", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, false);
		char[] source = sourceUnit.getSource().toCharArray();
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		assertTrue("Has error", compilationUnit.getProblems().length == 0); //$NON-NLS-1$
		ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("Malformed", !isMalformed(node));
		assertTrue("not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertTrue("not an assignment", expression.getNodeType() == ASTNode.ASSIGNMENT); //$NON-NLS-1$
		Assignment assignment = (Assignment) expression;
		checkSourceRange(assignment, "z= foo().y.toList()", source);
		Expression expression2 = assignment.getRightHandSide();
		checkSourceRange(expression2, "foo().y.toList()", source);
		assertTrue("not a method invocation", expression2.getNodeType() == ASTNode.FUNCTION_INVOCATION);
		FunctionInvocation methodInvocation = (FunctionInvocation) expression2;
		Expression expression3 = methodInvocation.getExpression();
		checkSourceRange(expression3, "foo().y", source);
		checkSourceRange(methodInvocation.getName(), "toList", source);
		assertTrue("not a field access", expression3.getNodeType() == ASTNode.FIELD_ACCESS);
		FieldAccess fieldAccess = (FieldAccess) expression3;
		checkSourceRange(fieldAccess.getName(), "y", source);
		Expression expression4 = fieldAccess.getExpression();
		checkSourceRange(expression4, "foo()", source);
		assertTrue("not a method invocation", expression4.getNodeType() == ASTNode.FUNCTION_INVOCATION);
		FunctionInvocation methodInvocation2 = (FunctionInvocation) expression4;
		checkSourceRange(methodInvocation2.getName(), "foo", source);
		assertNull("no null", methodInvocation2.getExpression());
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=32338
	 */
	public void test0462() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "", "Test462.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		assertTrue("Has error", compilationUnit.getProblems().length == 0); //$NON-NLS-1$
		ASTNode node = getASTNode(compilationUnit, 0);
		assertNotNull("No node", node);
		assertTrue("not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION);
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		assertEquals("Wrong name", "Test462", typeDeclaration.getName().getIdentifier());
		ITypeBinding typeBinding = typeDeclaration.resolveBinding();
		assertNotNull("No binding", typeBinding);
		assertEquals("Wrong name", "Test462", typeBinding.getQualifiedName());
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=33450
	 */
	public void test0463() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0463", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, false);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a return statement", node.getNodeType() == ASTNode.RETURN_STATEMENT); //$NON-NLS-1$
		ReturnStatement returnStatement = (ReturnStatement) node;
		Expression expression = returnStatement.getExpression();
		assertNotNull("No expression", expression);
		assertTrue("not a string literal", expression.getNodeType() == ASTNode.STRING_LITERAL); //$NON-NLS-1$
		StringLiteral stringLiteral = (StringLiteral) expression;
		checkSourceRange(stringLiteral, "\"\\012\\015\\u0061\"", source);
		assertEquals("wrong value", "\012\015a", stringLiteral.getLiteralValue());
		assertEquals("wrong value", "\"\\012\\015\\u0061\"", stringLiteral.getEscapedValue());
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=33039
	 */
	public void test0464() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0464", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
		assertEquals("No error", 1, compilationUnit.getProblems().length); //$NON-NLS-1$
		assertNotNull("No node", node);
		assertTrue("not a return statement", node.getNodeType() == ASTNode.RETURN_STATEMENT); //$NON-NLS-1$
		ReturnStatement returnStatement = (ReturnStatement) node;
		Expression expression = returnStatement.getExpression();
		assertNotNull("No expression", expression);
		assertTrue("not a null literal", expression.getNodeType() == ASTNode.NULL_LITERAL); //$NON-NLS-1$
		NullLiteral nullLiteral = (NullLiteral) expression;
		ITypeBinding typeBinding = nullLiteral.resolveTypeBinding();
		assertNotNull("No type binding", typeBinding);
		assertFalse("A primitive type", typeBinding.isPrimitive());
		assertTrue("Null type", typeBinding.isNullType());
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=33831
	 */
	public void test0465() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0465", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
		assertEquals("No error", 0, compilationUnit.getProblems().length); //$NON-NLS-1$
		assertNotNull("No node", node);
		assertTrue("not a return statement", node.getNodeType() == ASTNode.RETURN_STATEMENT); //$NON-NLS-1$
		ReturnStatement returnStatement = (ReturnStatement) node;
		Expression expression = returnStatement.getExpression();
		assertNotNull("No expression", expression);
		assertTrue("not a field access", expression.getNodeType() == ASTNode.FIELD_ACCESS); //$NON-NLS-1$
		FieldAccess fieldAccess = (FieldAccess) expression;
		Name name = fieldAccess.getName();
		IBinding binding = name.resolveBinding();
		assertNotNull("No binding", binding);
		assertEquals("Wrong type", IBinding.VARIABLE, binding.getKind());
		IVariableBinding variableBinding = (IVariableBinding) binding;
		assertEquals("Wrong name", "i", variableBinding.getName());
		assertEquals("Wrong type", "int", variableBinding.getType().getName());
		IVariableBinding variableBinding2 = fieldAccess.resolveFieldBinding();
		assertTrue("different binding", variableBinding == variableBinding2);
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=33949
	 */
	public void test0466() throws JavaScriptModelException {
		Hashtable options = JavaScriptCore.getOptions();
		Hashtable newOptions = JavaScriptCore.getOptions();
		try {
			newOptions.put(JavaScriptCore.COMPILER_SOURCE, JavaScriptCore.VERSION_1_4);
			JavaScriptCore.setOptions(newOptions);
			IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0466", "Assert.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			ASTNode result = runConversion(sourceUnit, true);
			JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
			char[] source = sourceUnit.getSource().toCharArray();
			ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
			checkSourceRange(node, "assert ref != null : message;", source);
			assertTrue("not an assert statement", node.getNodeType() == ASTNode.ASSERT_STATEMENT); //$NON-NLS-1$
			AssertStatement statement = (AssertStatement) node;
			checkSourceRange(statement.getExpression(), "ref != null", source);
			checkSourceRange(statement.getMessage(), "message", source);
		} finally {
			JavaScriptCore.setOptions(options);
		}
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=33949
	 */
	public void test0467() throws JavaScriptModelException {
		Hashtable options = JavaScriptCore.getOptions();
		Hashtable newOptions = JavaScriptCore.getOptions();
		try {
			newOptions.put(JavaScriptCore.COMPILER_SOURCE, JavaScriptCore.VERSION_1_4);
			JavaScriptCore.setOptions(newOptions);
			IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0467", "Assert.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			ASTNode result = runConversion(sourceUnit, true);
			JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
			char[] source = sourceUnit.getSource().toCharArray();
			ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
			checkSourceRange(node, "assert ref != null : message\\u003B", source);
			assertTrue("not an assert statement", node.getNodeType() == ASTNode.ASSERT_STATEMENT); //$NON-NLS-1$
			AssertStatement statement = (AssertStatement) node;
			checkSourceRange(statement.getExpression(), "ref != null", source);
			checkSourceRange(statement.getMessage(), "message", source);
			
			node = getASTNode(compilationUnit, 0, 0, 1);
			checkSourceRange(node, "assert ref != null\\u003B", source);
			assertTrue("not an assert statement", node.getNodeType() == ASTNode.ASSERT_STATEMENT); //$NON-NLS-1$
			statement = (AssertStatement) node;
			checkSourceRange(statement.getExpression(), "ref != null", source);
		} finally {
			JavaScriptCore.setOptions(options);
		}
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=36772
	 */
	public void test0468() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0468", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
		assertEquals("No error", 0, compilationUnit.getProblems().length); //$NON-NLS-1$
		assertNotNull("No node", node);
		assertTrue("not a return statement", node.getNodeType() == ASTNode.RETURN_STATEMENT); //$NON-NLS-1$
		ReturnStatement returnStatement = (ReturnStatement) node;
		Expression expression = returnStatement.getExpression();
		assertNotNull("No expression", expression);
		assertTrue("not a field access", expression.getNodeType() == ASTNode.FIELD_ACCESS); //$NON-NLS-1$
		FieldAccess fieldAccess = (FieldAccess) expression;
		Name name = fieldAccess.getName();
		IBinding binding = name.resolveBinding();
		assertNotNull("No binding", binding);
		assertEquals("Wrong type", IBinding.VARIABLE, binding.getKind());
		IVariableBinding variableBinding = (IVariableBinding) binding;
		assertEquals("Wrong name", "i", variableBinding.getName());
		assertEquals("Wrong type", "int", variableBinding.getType().getName());
		IVariableBinding variableBinding2 = fieldAccess.resolveFieldBinding();
		assertTrue("different binding", variableBinding == variableBinding2);
		
		node = getASTNode(compilationUnit, 0, 0);
		assertNotNull("No node", node);
		assertEquals("Wrong type", ASTNode.FIELD_DECLARATION, node.getNodeType());
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		List fragments = fieldDeclaration.fragments();
		assertEquals("wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		
		ASTNode foundNode = compilationUnit.findDeclaringNode(variableBinding);
		assertNotNull("No found node", foundNode);
		assertEquals("wrong node", fragment, foundNode);
	}		

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=36895
	 */
	public void test0469() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "codeManipulation", "bug.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 2, 0);
		assertEquals("No error", 0, compilationUnit.getProblems().length); //$NON-NLS-1$
		assertNotNull("No node", node);
		assertTrue("not a variable declaration statement", node.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		ASTNode parent = node.getParent();
		assertNotNull(parent);
		assertTrue("not a block", parent.getNodeType() == ASTNode.BLOCK); //$NON-NLS-1$
	}		

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=37381
	 */
	public void test0470() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0470", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
		assertEquals("No error", 0, compilationUnit.getProblems().length); //$NON-NLS-1$
		assertNotNull("No node", node);
		assertTrue("not a for statement", node.getNodeType() == ASTNode.FOR_STATEMENT); //$NON-NLS-1$
		ForStatement forStatement = (ForStatement) node;
		List initializers = forStatement.initializers();
		assertEquals("wrong size", 1, initializers.size());
		Expression initializer = (Expression) initializers.get(0);
		assertTrue("not a variable declaration expression", initializer.getNodeType() == ASTNode.VARIABLE_DECLARATION_EXPRESSION); //$NON-NLS-1$
		VariableDeclarationExpression variableDeclarationExpression = (VariableDeclarationExpression) initializer;
		List fragments = variableDeclarationExpression.fragments();
		assertEquals("wrong size", 2, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		checkSourceRange(fragment, "i= 0", source);
		fragment = (VariableDeclarationFragment) fragments.get(1);
		checkSourceRange(fragment, "j= goo(3)", source);
		checkSourceRange(variableDeclarationExpression, "int i= 0, j= goo(3)", source);
	}		

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=38447
	 */
	public void test0471() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0471", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		assertEquals("No error", 1, compilationUnit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(compilationUnit, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Is a constructor", !methodDeclaration.isConstructor());
		checkSourceRange(methodDeclaration, "private void foo(){", source);
		node = getASTNode(compilationUnit, 0, 1);
		assertNotNull("No node", node);
		assertTrue("not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Is a constructor", !methodDeclaration.isConstructor());
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=38447
	 */
	public void test0472() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "junit.textui", "ResultPrinter.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		assertEquals("No error", 2, compilationUnit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(compilationUnit, 0, 2);
		assertNotNull("No node", node);
		assertTrue("not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Not a constructor", methodDeclaration.isConstructor());
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=38732
	 */
	public void test0473() throws JavaScriptModelException {
		Hashtable options = JavaScriptCore.getOptions();
		Hashtable newOptions = JavaScriptCore.getOptions();
		try {
			newOptions.put(JavaScriptCore.COMPILER_SOURCE, JavaScriptCore.VERSION_1_4);
			newOptions.put(JavaScriptCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaScriptCore.ERROR);
			JavaScriptCore.setOptions(newOptions);
				
			IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0473", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			char[] source = sourceUnit.getSource().toCharArray();
			ASTNode result = runConversion(sourceUnit, true);
			JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
			assertEquals("No error", 2, compilationUnit.getProblems().length); //$NON-NLS-1$
			ASTNode node = getASTNode(compilationUnit, 0, 0, 0);
			assertNotNull("No node", node);
			assertTrue("not an assert statement", node.getNodeType() == ASTNode.ASSERT_STATEMENT); //$NON-NLS-1$
			AssertStatement assertStatement = (AssertStatement) node;
			checkSourceRange(assertStatement, "assert(true);", source);
			Expression expression = assertStatement.getExpression();
			checkSourceRange(expression, "(true)", source);
		} finally {
			JavaScriptCore.setOptions(options);
		}
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=39259
	 */
	public void test0474() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0474", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		assertEquals("No error", 0, compilationUnit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
		assertNotNull("No node", node);
		assertEquals("Not a while statement", node.getNodeType(), ASTNode.WHILE_STATEMENT);
		WhileStatement whileStatement = (WhileStatement) node;
		Statement statement = whileStatement.getBody();
		assertEquals("Not a while statement", statement.getNodeType(), ASTNode.WHILE_STATEMENT);
		WhileStatement whileStatement2 = (WhileStatement) statement;
		String expectedSource = 
			"while(b())\n" +
			"				foo();";
		checkSourceRange(whileStatement2, expectedSource, source);
		Statement statement2 = whileStatement2.getBody();
		checkSourceRange(statement2, "foo();", source);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=39259
	 */
	public void test0475() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0475", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		assertEquals("No error", 0, compilationUnit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
		assertNotNull("No node", node);
		assertEquals("Not an if statement", node.getNodeType(), ASTNode.IF_STATEMENT);
		IfStatement statement = (IfStatement) node;
		Statement statement2 = statement.getThenStatement();
		assertEquals("Not an if statement", statement2.getNodeType(), ASTNode.IF_STATEMENT);
		IfStatement statement3 = (IfStatement) statement2;
		String expectedSource = 
			"if(b())\n" +
			"				foo();";
		checkSourceRange(statement3, expectedSource, source);
		Statement statement4 = statement3.getThenStatement();
		checkSourceRange(statement4, "foo();", source);
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=39259
	 */
	public void test0476() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0476", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		assertEquals("No error", 0, compilationUnit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
		assertNotNull("No node", node);
		assertEquals("Not a for statement", node.getNodeType(), ASTNode.FOR_STATEMENT);
		ForStatement statement = (ForStatement) node;
		Statement statement2 = statement.getBody();
		assertEquals("Not a for statement", statement2.getNodeType(), ASTNode.FOR_STATEMENT);
		ForStatement statement3 = (ForStatement) statement2;
		String expectedSource = 
			"for(;b();)\n" +
			"				foo();";
		checkSourceRange(statement3, expectedSource, source);
		Statement statement4 = statement3.getBody();
		checkSourceRange(statement4, "foo();", source);
	}	

	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=39327
	 */
	public void test0477() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0477", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		assertEquals("No error", 1, compilationUnit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(compilationUnit, 0, 1, 0);
		assertNotNull("No node", node);

		checkSourceRange(node, "this(undef());", source);
		assertEquals("Not a constructor invocation", node.getNodeType(), ASTNode.CONSTRUCTOR_INVOCATION);		
		ConstructorInvocation constructorInvocation = (ConstructorInvocation) node;
		List arguments = constructorInvocation.arguments();
		assertEquals("Wrong size", 1, arguments.size());
		IFunctionBinding binding = constructorInvocation.resolveConstructorBinding();
		assertNotNull("No binding", binding);
	}

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=40474
	 */
	public void test0478() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0478", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 2, types.length);
		IType type = types[1];
		IFunction[] methods = type.getFunctions();
		assertNotNull(methods);
		assertEquals("wrong size", 1, methods.length);
		IFunction method = methods[0];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(sourceUnit, sourceRange.getOffset() + sourceRange.getLength() / 2, true);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 1, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "test", methodDeclaration.getName().getIdentifier());
		IFunctionBinding methodBinding = methodDeclaration.resolveBinding();
		assertNotNull(methodBinding);
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 2, statements.size());
		ASTNode node2 = (ASTNode) statements.get(1);
		assertNotNull(node2);
		assertTrue("Not an expression statement", node2.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node2;
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		assertTrue("Not a simple name", expression2.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) expression2;
		IBinding binding  = simpleName.resolveBinding();
		assertNotNull("No binding", binding); //$NON-NLS-1$
		assertTrue("wrong type", binding.getKind() == IBinding.VARIABLE); //$NON-NLS-1$
		IVariableBinding variableBinding = (IVariableBinding) binding;
		assertEquals("Wrong name", "a", variableBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		SimpleName simpleName2 = methodInvocation.getName();
		assertEquals("Wrong name", "clone", simpleName2.getIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
		IBinding binding2 = simpleName2.resolveBinding();
		assertNotNull("no binding2", binding2); //$NON-NLS-1$
		assertTrue("Wrong type", binding2.getKind() == IBinding.METHOD); //$NON-NLS-1$
		IFunctionBinding methodBinding2 = (IFunctionBinding) binding2;
		assertEquals("Wrong name", "clone", methodBinding2.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}	

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=40474
	 */
	public void test0479() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0479", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 2, types.length);
		IType type = types[1];
		IFunction[] methods = type.getFunctions();
		assertNotNull(methods);
		assertEquals("wrong size", 1, methods.length);
		IFunction method = methods[0];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(sourceUnit, sourceRange.getOffset() + sourceRange.getLength() / 2, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 1, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "test", methodDeclaration.getName().getIdentifier());
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 2, statements.size());
		ASTNode node2 = (ASTNode) statements.get(1);
		assertNotNull(node2);
		assertTrue("Not an expression statement", node2.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node2;
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		assertTrue("Not a simple name", expression2.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) expression2;
		IBinding binding  = simpleName.resolveBinding();
		assertNull("No binding", binding); //$NON-NLS-1$
		SimpleName simpleName2 = methodInvocation.getName();
		assertEquals("Wrong name", "clone", simpleName2.getIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=40474
	 */
	public void test0480() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0480", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 1, types.length);
		IType type = types[0];
		IFunction[] methods = type.getFunctions();
		assertNotNull(methods);
		assertEquals("wrong size", 1, methods.length);
		IFunction method = methods[0];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(sourceUnit, sourceRange.getOffset() + sourceRange.getLength() / 2, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "test", methodDeclaration.getName().getIdentifier());
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 1, statements.size());
		ASTNode node2 = (ASTNode) statements.get(0);
		assertNotNull(node2);
		assertTrue("Not an variable declaration statement", node2.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=40474
	 */
	public void test0481() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0481", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 1, types.length);
		IType type = types[0];
		IFunction[] methods = type.getFunctions();
		assertNotNull(methods);
		assertEquals("wrong size", 1, methods.length);
		IFunction method = methods[0];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(sourceUnit, sourceRange.getOffset() + sourceRange.getLength() / 2, true);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "test", methodDeclaration.getName().getIdentifier());
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 1, statements.size());
		ASTNode node2 = (ASTNode) statements.get(0);
		assertNotNull(node2);
		assertTrue("Not an variable declaration statement", node2.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node2;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertTrue("Not a class instance creation", expression.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
		ITypeBinding typeBinding = classInstanceCreation.resolveTypeBinding();
		assertNotNull(typeBinding);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=40474
	 */
	public void test0482() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0482", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 1, types.length);
		IType type = types[0];
		IType[] memberTypes = type.getTypes();
		assertNotNull(memberTypes);
		assertEquals("wrong size", 1, memberTypes.length);
		IType memberType = memberTypes[0];
		IFunction[] methods = memberType.getFunctions();
		assertEquals("wrong size", 1, methods.length);
		IFunction method = methods[0];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(sourceUnit, sourceRange.getOffset() + sourceRange.getLength() / 2, true);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "test", methodDeclaration.getName().getIdentifier());
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 1, statements.size());
		ASTNode node2 = (ASTNode) statements.get(0);
		assertNotNull(node2);
		assertTrue("Not an variable declaration statement", node2.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node2;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertTrue("Not a class instance creation", expression.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
		ITypeBinding typeBinding = classInstanceCreation.resolveTypeBinding();
		assertNotNull(typeBinding);
		assertTrue(typeBinding.isAnonymous());
		assertEquals("Wrong name", "", typeBinding.getName());
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=40474
	 */
	public void test0483() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0483", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 1, types.length);
		IType type = types[0];
		IFunction[] methods = type.getFunctions();
		assertEquals("wrong size", 1, methods.length);
		IFunction method = methods[0];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(sourceUnit, sourceRange.getOffset() + sourceRange.getLength() / 2, true);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "A", methodDeclaration.getName().getIdentifier());
		assertTrue("Not a constructor", methodDeclaration.isConstructor());
		IBinding binding = methodDeclaration.getName().resolveBinding();
		assertNotNull(binding);
		assertEquals("Wrong type", IBinding.METHOD, binding.getKind());
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 1, statements.size());
		ASTNode node2 = (ASTNode) statements.get(0);
		assertNotNull(node2);
		assertTrue("Not an variable declaration statement", node2.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node2;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertTrue("Not a class instance creation", expression.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
		ITypeBinding typeBinding = classInstanceCreation.resolveTypeBinding();
		assertNotNull(typeBinding);
		assertTrue(typeBinding.isAnonymous());
		assertEquals("Wrong name", "", typeBinding.getName());
	}

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=40474
	 */
	public void test0484() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0482", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 1, types.length);
		IType type = types[0];
		IType[] memberTypes = type.getTypes();
		assertNotNull(memberTypes);
		assertEquals("wrong size", 1, memberTypes.length);
		IType memberType = memberTypes[0];
		ISourceRange sourceRange = memberType.getSourceRange(); 
		ASTNode result = runConversion(sourceUnit, sourceRange.getOffset() + sourceRange.getLength() / 2, true);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION); //$NON-NLS-1$
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		assertEquals("wrong name", "B", typeDeclaration.getName().getIdentifier());
		List bodyDeclarations = typeDeclaration.bodyDeclarations();
		assertEquals("Wrong size", 1, bodyDeclarations.size());
		BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
		assertTrue("Not a method declaration", bodyDeclaration.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) bodyDeclaration;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("Wrong size", 1, statements.size());
	}

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=40474
	 */
	public void test0485() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0482", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 1, types.length);
		IType type = types[0];
		IType[] memberTypes = type.getTypes();
		assertNotNull(memberTypes);
		assertEquals("wrong size", 1, memberTypes.length);
		IType memberType = memberTypes[0];
		ISourceRange sourceRange = memberType.getSourceRange(); 
		ASTNode result = runConversion(sourceUnit, sourceRange.getOffset() + sourceRange.getLength() / 2, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION); //$NON-NLS-1$
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		assertEquals("wrong name", "B", typeDeclaration.getName().getIdentifier());
		List bodyDeclarations = typeDeclaration.bodyDeclarations();
		assertEquals("Wrong size", 1, bodyDeclarations.size());
		BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
		assertTrue("Not a method declaration", bodyDeclaration.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) bodyDeclaration;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("Wrong size", 1, statements.size());
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=40474
	 */
	public void test0486() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0486", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 1, types.length);
		IType type = types[0];
		IFunction[] methods = type.getFunctions();
		assertEquals("wrong size", 2, methods.length);
		IFunction method = methods[1];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(sourceUnit, sourceRange.getOffset() + sourceRange.getLength() / 2, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 2);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("Wrong size", 2, statements.size());

		node = getASTNode((JavaScriptUnit) result, 0, 1);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		methodDeclaration = (FunctionDeclaration) node;
		block = methodDeclaration.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
	}

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=40474
	 */
	public void test0487() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0487", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 1, types.length);
		IType type = types[0];
		IFunction[] methods = type.getFunctions();
		assertEquals("wrong size", 3, methods.length);
		IFunction method = methods[1];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(sourceUnit, sourceRange.getOffset() + sourceRange.getLength() / 2, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$

		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 5);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("Wrong size", 2, statements.size());

		node = getASTNode((JavaScriptUnit) result, 0, 4);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		methodDeclaration = (FunctionDeclaration) node;
		block = methodDeclaration.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		List fragments = fieldDeclaration.fragments();
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Wrong name", "field", fragment.getName().getIdentifier());
		assertNotNull("No initializer", expression);

		node = getASTNode((JavaScriptUnit) result, 0, 1);
		assertTrue("Not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		fragment = (VariableDeclarationFragment) fragments.get(0);
		expression = fragment.getInitializer();
		assertEquals("Wrong name", "i", fragment.getName().getIdentifier());
		assertNotNull("No initializer", expression);

		node = getASTNode((JavaScriptUnit) result, 0, 2);
		assertTrue("Not an initializer", node.getNodeType() == ASTNode.INITIALIZER); //$NON-NLS-1$
		Initializer initializer = (Initializer) node;
		assertEquals("Not static", Modifier.NONE, initializer.getModifiers());
		block = initializer.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 3);
		assertTrue("Not an initializer", node.getNodeType() == ASTNode.INITIALIZER); //$NON-NLS-1$
		initializer = (Initializer) node;
		assertEquals("Not static", Modifier.STATIC, initializer.getModifiers());
		block = initializer.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 6);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		methodDeclaration = (FunctionDeclaration) node;
		block = methodDeclaration.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
	}	

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=40474
	 */
	public void test0488() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0488", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 1, types.length);
		IType type = types[0];
		IInitializer[] initializers = type.getInitializers();
		assertEquals("wrong size", 2, initializers.length);
		IInitializer init = initializers[1];
		ISourceRange sourceRange = init.getSourceRange(); 
		ASTNode result = runConversion(sourceUnit, sourceRange.getOffset() + sourceRange.getLength() / 2, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$

		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 5);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());

		node = getASTNode((JavaScriptUnit) result, 0, 4);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		methodDeclaration = (FunctionDeclaration) node;
		block = methodDeclaration.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		List fragments = fieldDeclaration.fragments();
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Wrong name", "field", fragment.getName().getIdentifier());
		assertNotNull("No initializer", expression);

		node = getASTNode((JavaScriptUnit) result, 0, 1);
		assertTrue("Not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		fragment = (VariableDeclarationFragment) fragments.get(0);
		expression = fragment.getInitializer();
		assertEquals("Wrong name", "i", fragment.getName().getIdentifier());
		assertNotNull("No initializer", expression);

		node = getASTNode((JavaScriptUnit) result, 0, 2);
		assertTrue("Not an initializer", node.getNodeType() == ASTNode.INITIALIZER); //$NON-NLS-1$
		Initializer initializer = (Initializer) node;
		assertEquals("Not static", Modifier.NONE, initializer.getModifiers());
		block = initializer.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 3);
		assertTrue("Not an initializer", node.getNodeType() == ASTNode.INITIALIZER); //$NON-NLS-1$
		initializer = (Initializer) node;
		assertEquals("Not static", Modifier.STATIC, initializer.getModifiers());
		block = initializer.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 1, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 6);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		methodDeclaration = (FunctionDeclaration) node;
		block = methodDeclaration.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
	}	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=40804
	 */
	public void test0489() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0489", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION); //$NON-NLS-1$
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		assertNull("Got a type binding", typeDeclaration.resolveBinding()); 
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=40804
	 */
	public void test0490() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0490", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=42647
	 */
	public void test0491() throws JavaScriptModelException {
		Hashtable options = JavaScriptCore.getOptions();
		Hashtable newOptions = JavaScriptCore.getOptions();
		try {
			newOptions.put(JavaScriptCore.COMPILER_SOURCE, JavaScriptCore.VERSION_1_4);
			JavaScriptCore.setOptions(newOptions);
			IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0491", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			char[] source = sourceUnit.getSource().toCharArray();
			ASTNode result = runConversion(sourceUnit, true);
			assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
			JavaScriptUnit unit = (JavaScriptUnit) result;
			assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
			ASTNode node = getASTNode(unit, 0, 0, 0);
			assertTrue("not an assert statement", node.getNodeType() == ASTNode.ASSERT_STATEMENT); //$NON-NLS-1$
			AssertStatement assertStatement = (AssertStatement) node;
			Expression expression = assertStatement.getExpression();
			assertTrue("not a parenthesized expression", expression.getNodeType() == ASTNode.PARENTHESIZED_EXPRESSION); //$NON-NLS-1$
			checkSourceRange(expression, "(loginName != null)", source);
		} finally {
			JavaScriptCore.setOptions(options);
		}
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=42647
	 */
	public void test0492() throws JavaScriptModelException {
		Hashtable options = JavaScriptCore.getOptions();
		Hashtable newOptions = JavaScriptCore.getOptions();
		try {
			newOptions.put(JavaScriptCore.COMPILER_SOURCE, JavaScriptCore.VERSION_1_4);
			JavaScriptCore.setOptions(newOptions);
			IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0492", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			char[] source = sourceUnit.getSource().toCharArray();
			ASTNode result = runConversion(sourceUnit, true);
			assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
			JavaScriptUnit unit = (JavaScriptUnit) result;
			assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
			ASTNode node = getASTNode(unit, 0, 0, 0);
			assertTrue("not an assert statement", node.getNodeType() == ASTNode.ASSERT_STATEMENT); //$NON-NLS-1$
			AssertStatement assertStatement = (AssertStatement) node;
			Expression expression = assertStatement.getExpression();
			checkSourceRange(expression, "loginName != null", source);
		} finally {
			JavaScriptCore.setOptions(options);
		}
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=42839
	 */
	public void test0493() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0493", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0);
		assertTrue("not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		Type type = fieldDeclaration.getType();
		checkSourceRange(type, "Class[][]", source);
		assertTrue("not an array type", type.isArrayType()); //$NON-NLS-1$
		ArrayType arrayType = (ArrayType) type;
		Type componentType = arrayType.getComponentType();
		assertTrue("not an array type", componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class[]", source);
		arrayType = (ArrayType) componentType;
		componentType = arrayType.getComponentType();
		assertTrue("is an array type", !componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class", source);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=42839
	 */
	public void test0494() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0494", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0);
		assertTrue("not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		Type type = fieldDeclaration.getType();
		checkSourceRange(type, "Class[][][]", source);
		assertTrue("not an array type", type.isArrayType()); //$NON-NLS-1$
		ArrayType arrayType = (ArrayType) type;
		Type componentType = arrayType.getComponentType();
		assertTrue("not an array type", componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class[][]", source);
		arrayType = (ArrayType) componentType;
		componentType = arrayType.getComponentType();
		assertTrue("not an array type", componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class[]", source);
		arrayType = (ArrayType) componentType;
		componentType = arrayType.getComponentType();
		assertTrue("is an array type", !componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class", source);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=42839
	 */
	public void test0495() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0495", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0);
		assertTrue("not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		Type type = fieldDeclaration.getType();
		checkSourceRange(type, "Class[][]", source);
		assertTrue("not an array type", type.isArrayType()); //$NON-NLS-1$
		ArrayType arrayType = (ArrayType) type;
		Type componentType = arrayType.getComponentType();
		assertTrue("not an array type", componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class[]", source);
		arrayType = (ArrayType) componentType;
		componentType = arrayType.getComponentType();
		assertTrue("is an array type", !componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class", source);
		List fragments = fieldDeclaration.fragments();
		assertEquals("wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		assertEquals("wrong extra dimension", 1, fragment.getExtraDimensions());
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=42839
	 */
	public void test0496() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0496", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0);
		assertTrue("not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		Type type = fieldDeclaration.getType();
		checkSourceRange(type, "Class[][][][]", source);
		assertTrue("not an array type", type.isArrayType()); //$NON-NLS-1$
		ArrayType arrayType = (ArrayType) type;
		Type componentType = arrayType.getComponentType();
		assertTrue("not an array type", componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class[][][]", source);
		arrayType = (ArrayType) componentType;
		componentType = arrayType.getComponentType();
		assertTrue("not an array type", componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class[][]", source);
		arrayType = (ArrayType) componentType;
		componentType = arrayType.getComponentType();
		assertTrue("not an array type", componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class[]", source);
		arrayType = (ArrayType) componentType;
		componentType = arrayType.getComponentType();
		assertTrue("is an array type", !componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class", source);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=42839
	 */
	public void test0497() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0497", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$<
		ASTNode node = getASTNode(unit, 0, 0);
		assertTrue("not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		Type type = fieldDeclaration.getType();
		checkSourceRange(type, "Class[]", source);
		assertTrue("not an array type", type.isArrayType()); //$NON-NLS-1$
		ArrayType arrayType = (ArrayType) type;
		Type componentType = arrayType.getComponentType();
		assertTrue("is an array type", !componentType.isArrayType()); //$NON-NLS-1$
		checkSourceRange(componentType, "Class", source);
	}

	/**
	 */
	public void test0498() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0498", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=45199
	 */
	public void test0499() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0499", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 1);
		assertNotNull(node);
		assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		Expression expression = ((ExpressionStatement) node).getExpression();
		assertTrue("Not an assignment", expression.getNodeType() == ASTNode.ASSIGNMENT); //$NON-NLS-1$
		Assignment assignment = (Assignment) expression;
		Expression expression2 = assignment.getRightHandSide();
		assertTrue("Not an infix expression", expression2.getNodeType() == ASTNode.INFIX_EXPRESSION); //$NON-NLS-1$
		InfixExpression infixExpression = (InfixExpression) expression2;
		Expression expression3 = infixExpression.getLeftOperand();
		assertTrue("Not a simple name", expression3.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		ITypeBinding binding = expression3.resolveTypeBinding();
		assertNotNull("No binding", binding);
		Expression expression4 = assignment.getLeftHandSide();
		assertTrue("Not a simple name", expression4.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		ITypeBinding binding2 = expression4.resolveTypeBinding();
		assertNotNull("No binding", binding2);
		assertTrue("Should be the same", binding == binding2);
	}
	
	/**
	 * Test for bug 45436 fix.
	 * When this bug happened, the first assertion was false (2 problems found).
	 * @see <a href="http://bugs.eclipse.org/bugs/show_bug.cgi?id=45436">bug 45436</a>
	 * @throws JavaScriptModelException
	 */
	public void test0500() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0500", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IJavaScriptProject project = sourceUnit.getJavaScriptProject();
		Map originalOptions = project.getOptions(false);
		try {
			project.setOption(JavaScriptCore.COMPILER_PB_INVALID_JAVADOC, JavaScriptCore.ERROR);
			project.setOption(JavaScriptCore.COMPILER_PB_MISSING_JAVADOC_TAGS, JavaScriptCore.ERROR);
			project.setOption(JavaScriptCore.COMPILER_PB_MISSING_JAVADOC_COMMENTS, JavaScriptCore.ERROR);
			JavaScriptUnit result = (JavaScriptUnit)runConversion(sourceUnit, true);
			IProblem[] problems= result.getProblems();
			assertTrue(problems.length == 1);
			assertEquals("Invalid warning", "Javadoc: Missing tag for parameter a", problems[0].getMessage());
		} finally {
			project.setOptions(originalOptions);
		}
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46012
	 */
	public void test0501() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0501", "JavaEditor.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, false);
		assertNotNull(result);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46013
	 */
	public void test0502a() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0502", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// 'i' in initializer
		VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement)getASTNode(unit, 0, 0, 0);
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) variableDeclarationStatement.fragments().get(0);
		IVariableBinding localBinding = fragment.resolveBinding();
		assertEquals("Unexpected key", "Ltest0502/A;#0#i", localBinding.getKey()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46013
	 */
	public void test0502b() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0502", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// 'j' in 'then' block in initializer
		IfStatement ifStatement = (IfStatement) getASTNode(unit, 0, 0, 1);
		Block block = (Block)ifStatement.getThenStatement();
		VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) block.statements().get(0);
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) variableDeclarationStatement.fragments().get(0);
		IVariableBinding localBinding = fragment.resolveBinding();
		assertEquals("Unexpected key", "Ltest0502/A;#0#0#j", localBinding.getKey()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46013
	 */
	public void test0502c() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0502", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// 'i' in 'foo()'
		VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement)getASTNode(unit, 0, 1, 0);
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) variableDeclarationStatement.fragments().get(0);
		IVariableBinding localBinding = fragment.resolveBinding();
		assertEquals("Unexpected key", "Ltest0502/A;.foo()V#i", localBinding.getKey()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46013
	 */
	public void test0502d() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0502", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// 'j' in 'then' block in 'foo()'
		IfStatement ifStatement = (IfStatement) getASTNode(unit, 0, 1, 1);
		Block block = (Block)ifStatement.getThenStatement();
		VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) block.statements().get(0);
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) variableDeclarationStatement.fragments().get(0);
		IVariableBinding localBinding = fragment.resolveBinding();
		assertEquals("Unexpected key", "Ltest0502/A;.foo()V#0#j", localBinding.getKey()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46013
	 */
	public void test0502e() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0502", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// 'j' in 'else' block in 'foo()'
		IfStatement ifStatement = (IfStatement) getASTNode(unit, 0, 1, 1);
		Block block = (Block)ifStatement.getElseStatement();
		VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) block.statements().get(0);
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) variableDeclarationStatement.fragments().get(0);
		IVariableBinding localBinding = fragment.resolveBinding();
		assertEquals("Unexpected key", "Ltest0502/A;.foo()V#1#j", localBinding.getKey()); //$NON-NLS-1$
	}
	
	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46013
	 */
	public void test0502f() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0502", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// first 'new Object(){...}' in 'foo()'
		ExpressionStatement expressionStatement = (ExpressionStatement) getASTNode(unit, 0, 1, 2);
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
		ITypeBinding typeBinding = anonymousClassDeclaration.resolveBinding();
		assertEquals("Unexpected key", "Ltest0502/A$182;", typeBinding.getKey()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46013
	 * @deprecated using deprecated code
	 */
	public void test0502g() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0502", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// 'B' in 'foo()'
		TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) getASTNode(unit, 0, 1, 3);
		TypeDeclaration typeDeclaration = typeDeclarationStatement.getTypeDeclaration();
		ITypeBinding typeBinding = typeDeclaration.resolveBinding();
		assertEquals("Unexpected key", "Ltest0502/A$206$B;", typeBinding.getKey()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46013
	 */
	public void test0502h() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0502", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// second 'new Object(){...}' in 'foo()'
		ExpressionStatement expressionStatement = (ExpressionStatement) getASTNode(unit, 0, 1, 4);
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
		ITypeBinding typeBinding = anonymousClassDeclaration.resolveBinding();
		assertEquals("Unexpected key", "Ltest0502/A$255;", typeBinding.getKey()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46013
	 * @deprecated using deprecated code
	 */
	public void test0502i() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0502", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// 'field' in 'B' in 'foo()'
		TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) getASTNode(unit, 0, 1, 3);
		TypeDeclaration typeDeclaration = typeDeclarationStatement.getTypeDeclaration();
		FieldDeclaration fieldDeclaration = typeDeclaration.getFields()[0];
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
		IVariableBinding fieldBinding = fragment.resolveBinding();
		assertEquals("Unexpected key", "Ltest0502/A$206$B;.field)I", fieldBinding.getKey()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46013
	 * @deprecated using deprecated code
	 */
	public void test0502j() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0502", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// 'bar()' in 'B' in 'foo()'
		TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) getASTNode(unit, 0, 1, 3);
		TypeDeclaration typeDeclaration = typeDeclarationStatement.getTypeDeclaration();
		FunctionDeclaration methodDeclaration = typeDeclaration.getMethods()[0];
		IFunctionBinding methodBinding = methodDeclaration.resolveBinding();
		assertEquals("Unexpected key", "Ltest0502/A$206$B;.bar()V", methodBinding.getKey()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46057
	 */
	public void test0503a() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0503", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// top level type A
		TypeDeclaration type = (TypeDeclaration)getASTNode(unit, 0);
		ITypeBinding typeBinding = type.resolveBinding();
		assertEquals("Unexpected binary name", "test0503.A", typeBinding.getBinaryName()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46057
	 */
	public void test0503b() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0503", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// member type B in A
		TypeDeclaration type = (TypeDeclaration)getASTNode(unit, 0, 0);
		ITypeBinding typeBinding = type.resolveBinding();
		assertEquals("Unexpected binary name", "test0503.A$B", typeBinding.getBinaryName()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46057
	 * @deprecated using deprecated code
	 */
	public void test0503c() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0503", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// local type E in foo() in A
		TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) getASTNode(unit, 0, 1, 0);
		TypeDeclaration typeDeclaration = typeDeclarationStatement.getTypeDeclaration();
		ITypeBinding typeBinding = typeDeclaration.resolveBinding();
		assertEquals("Unexpected binary name", "test0503.A$1$E", typeBinding.getBinaryName()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46057
	 */
	public void test0503d() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0503", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// anonymous type new Object() {...} in foo() in A
		ExpressionStatement expressionStatement = (ExpressionStatement) getASTNode(unit, 0, 1, 1);
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
		ITypeBinding typeBinding = anonymousClassDeclaration.resolveBinding();
		assertEquals("Unexpected binary name", "test0503.A$2", typeBinding.getBinaryName()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46057
	 */
	public void test0503e() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0503", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// type F in anonymous type new Object() {...} in foo() in A
		ExpressionStatement expressionStatement = (ExpressionStatement) getASTNode(unit, 0, 1, 1);
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
		TypeDeclaration type = (TypeDeclaration) anonymousClassDeclaration.bodyDeclarations().get(0);
		ITypeBinding typeBinding = type.resolveBinding();
		assertEquals("Unexpected binary name", "test0503.A$2$F", typeBinding.getBinaryName()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46057
	 * @deprecated using deprecated code
	 */
	public void test0503f() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0503", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// local type C in bar() in B in A
		FunctionDeclaration method = (FunctionDeclaration) getASTNode(unit, 0, 0, 0);
		TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) method.getBody().statements().get(0);
		TypeDeclaration typeDeclaration = typeDeclarationStatement.getTypeDeclaration();
		ITypeBinding typeBinding = typeDeclaration.resolveBinding();
		assertEquals("Unexpected binary name", "test0503.A$1$C", typeBinding.getBinaryName()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46057
	 */
	public void test0503g() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0503", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// anonymous type new Object() {...} in bar() in B in A
		FunctionDeclaration method = (FunctionDeclaration) getASTNode(unit, 0, 0, 0);
		ExpressionStatement expressionStatement = (ExpressionStatement) method.getBody().statements().get(1);
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
		ITypeBinding typeBinding = anonymousClassDeclaration.resolveBinding();
		assertEquals("Unexpected binary name", "test0503.A$1", typeBinding.getBinaryName()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46057
	 */
	public void test0503h() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0503", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// type D in anonymous type new Object() {...} in bar() in B in A
		FunctionDeclaration method = (FunctionDeclaration) getASTNode(unit, 0, 0, 0);
		ExpressionStatement expressionStatement = (ExpressionStatement) method.getBody().statements().get(1);
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
		TypeDeclaration type = (TypeDeclaration) anonymousClassDeclaration.bodyDeclarations().get(0);
		ITypeBinding typeBinding = type.resolveBinding();
		assertEquals("Unexpected binary name", "test0503.A$1$D", typeBinding.getBinaryName()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=46057
	 * @deprecated using deprecated code
	 */
	public void test0503i() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0503", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		JavaScriptUnit unit = (JavaScriptUnit)runConversion(sourceUnit, true);
		
		// unreachable type G in foo() in A
		IfStatement ifStatement = (IfStatement) getASTNode(unit, 0, 1, 2);
		Block block = (Block)ifStatement.getThenStatement();
		TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) block.statements().get(0);
		TypeDeclaration typeDeclaration = typeDeclarationStatement.getTypeDeclaration();
		ITypeBinding typeBinding = typeDeclaration.resolveBinding();
		assertEquals("Unexpected binary name", null, typeBinding.getBinaryName()); //$NON-NLS-1$
	}	

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=47396
	 */
	public void test0504() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0504", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 1, 0);
		assertNotNull(node);
		assertTrue("Not a constructor declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration declaration = (FunctionDeclaration) node;
		assertTrue("A constructor", !declaration.isConstructor());
		checkSourceRange(declaration, "public method(final int parameter);", source);
	}

	/**
	 * http://bugs.eclipse.org/bugs/show_bug.cgi?id=47396
	 */
	public void test0505() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0505", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertTrue("not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 1, 0);
		assertNotNull(node);
		assertTrue("Not a constructor declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration declaration = (FunctionDeclaration) node;
		assertTrue("A constructor", !declaration.isConstructor());
		checkSourceRange(declaration, "public method(final int parameter) {     }", source);
	}

	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=46699
	 */
	public void test0506() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0506", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong number of problems", 0, (unit).getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		assertTrue("Not a class instance creation", expressionStatement.getExpression().getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		IFunctionBinding binding = classInstanceCreation.resolveConstructorBinding();
		assertFalse("is synthetic", binding.isSynthetic());
		assertTrue("is default constructor", binding.isDefaultConstructor());
		assertNull("Has a declaring node", unit.findDeclaringNode(binding));
	}

	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=46699
	 */
	public void test0507() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0507", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong number of problems", 0, (unit).getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		assertTrue("Not a class instance creation", expressionStatement.getExpression().getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		IFunctionBinding binding = classInstanceCreation.resolveConstructorBinding();
		assertFalse("is synthetic", binding.isSynthetic());
		assertTrue("is default constructor", binding.isDefaultConstructor());
		assertNull("Has a declaring node", unit.findDeclaringNode(binding));
	}

	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=46699
	 */
	public void test0508() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0508", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(unit, 0, 1, 0);
		assertEquals("Wrong number of problems", 0, (unit).getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		assertTrue("Not a class instance creation", expressionStatement.getExpression().getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		IFunctionBinding binding = classInstanceCreation.resolveConstructorBinding();
		assertFalse("is synthetic", binding.isSynthetic());
		assertTrue("not a default constructor", !binding.isDefaultConstructor());
		assertNotNull("Has no declaring node", unit.findDeclaringNode(binding));
	}

	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=46699
	 */
	public void test0509() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0509", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong number of problems", 0, (unit).getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		assertTrue("Not a class instance creation", expressionStatement.getExpression().getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		IFunctionBinding binding = classInstanceCreation.resolveConstructorBinding();
		assertFalse("is synthetic", binding.isSynthetic());
		assertTrue("not a default constructor", !binding.isDefaultConstructor());
		assertNull("Has a declaring node", unit.findDeclaringNode(binding));
	}

	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=46699
	 */
	public void test0510() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0510", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong number of problems", 0, (unit).getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		assertTrue("Not a class instance creation", expressionStatement.getExpression().getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		IFunctionBinding binding = classInstanceCreation.resolveConstructorBinding();
		assertFalse("is synthetic", binding.isSynthetic());
		assertFalse("is default constructor", binding.isDefaultConstructor());
		assertNull("Has a declaring node", unit.findDeclaringNode(binding));
	}

	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=46699
	 */
	public void test0511() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0511", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong number of problems", 0, (unit).getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		assertTrue("Not a class instance creation", expressionStatement.getExpression().getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expressionStatement.getExpression();
		IFunctionBinding binding = classInstanceCreation.resolveConstructorBinding();
		assertFalse("is synthetic", binding.isSynthetic());
		assertFalse("is default constructor", binding.isDefaultConstructor());
		assertNull("Has a declaring node", unit.findDeclaringNode(binding));
	}

	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=47326
	 */
	public void test0512() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0512", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		ASTNode node = getASTNode(unit, 0, 0);
		assertEquals("Wrong number of problems", 2, unit.getProblems().length); //$NON-NLS-1$
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration declaration = (FunctionDeclaration) node;
		assertTrue("Not a constructor", declaration.isConstructor());
		checkSourceRange(declaration, "public A();", source);
	}
	
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=49429
	 */
	public void test0513() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0513", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$
	}
	
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=48502
	 */
	public void test0514() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0514", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$
	}
	
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=49204
	 */
	public void test0515() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0515", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a if statement", node.getNodeType() == ASTNode.IF_STATEMENT);
		IfStatement ifStatement = (IfStatement) node;
		assertTrue("not an empty statement", ifStatement.getThenStatement().getNodeType() == ASTNode.EMPTY_STATEMENT);
		checkSourceRange(ifStatement.getThenStatement(), ";", source);
		Statement statement = ifStatement.getElseStatement();
		assertTrue("not a if statement", statement.getNodeType() == ASTNode.IF_STATEMENT);
		ifStatement = (IfStatement) statement;
		assertTrue("not an empty statement", ifStatement.getThenStatement().getNodeType() == ASTNode.EMPTY_STATEMENT);
		checkSourceRange(ifStatement.getThenStatement(), ";", source);
		Statement statement2 = ifStatement.getElseStatement();
		assertTrue("not an empty statement", statement2.getNodeType() == ASTNode.EMPTY_STATEMENT);
		checkSourceRange(statement2, ";", source);
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=48489
	 * @deprecated using deprecated code
	 */
	public void test0516() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0516", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, false);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION);
		FunctionDeclaration declaration = (FunctionDeclaration) node;
		ASTParser parser = ASTParser.newParser(AST.JLS2);
		parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
		parser.setSource(source);
		parser.setSourceRange(declaration.getStartPosition(), declaration.getLength());
		parser.setCompilerOptions(JavaScriptCore.getOptions());
		ASTNode result2 = parser.createAST(null);
		assertNotNull("No node", result2);
		assertTrue("not a type declaration", result2.getNodeType() == ASTNode.TYPE_DECLARATION);
		TypeDeclaration typeDeclaration = (TypeDeclaration) result2;
		List bodyDeclarations = typeDeclaration.bodyDeclarations();
		assertEquals("wrong size", 1, bodyDeclarations.size());
		BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
		assertTrue(declaration.subtreeMatch(new ASTMatcher(), bodyDeclaration));
		ASTNode root = bodyDeclaration.getRoot();
		assertNotNull("No root", root);
		assertTrue("not a compilation unit", root.getNodeType() == ASTNode.JAVASCRIPT_UNIT);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) root;
		assertEquals("wrong problem size", 0, compilationUnit.getProblems().length);
		assertNotNull("No comments", compilationUnit.getCommentList());
		assertEquals("Wrong size", 3, compilationUnit.getCommentList().size());
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=48489
	 * @deprecated using deprecated code
	 */
	public void test0517() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0517", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, false);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		assertNotNull("No comments", unit.getCommentList());
		assertEquals("Wrong size", 3, unit.getCommentList().size());
		ASTNode node = getASTNode(unit, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION);
		FieldDeclaration declaration = (FieldDeclaration) node;
		ASTParser parser = ASTParser.newParser(AST.JLS2);
		parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
		parser.setSource(source);
		parser.setSourceRange(declaration.getStartPosition(), declaration.getLength());
		parser.setCompilerOptions(JavaScriptCore.getOptions());
		ASTNode result2 = parser.createAST(null);
		assertNotNull("No node", result2);
		assertTrue("not a type declaration", result2.getNodeType() == ASTNode.TYPE_DECLARATION);
		TypeDeclaration typeDeclaration = (TypeDeclaration) result2;
		List bodyDeclarations = typeDeclaration.bodyDeclarations();
		assertEquals("wrong size", 1, bodyDeclarations.size());
		BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
		assertTrue(declaration.subtreeMatch(new ASTMatcher(), bodyDeclaration));
		ASTNode root = bodyDeclaration.getRoot();
		assertNotNull("No root", root);
		assertTrue("not a compilation unit", root.getNodeType() == ASTNode.JAVASCRIPT_UNIT);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) root;
		assertEquals("wrong problem size", 0, compilationUnit.getProblems().length);
		assertNotNull("No comments", compilationUnit.getCommentList());
		assertEquals("Wrong size", 2, compilationUnit.getCommentList().size());
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=48489
	 * @deprecated using deprecated code
	 */
	public void test0518() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0518", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, false);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not an initializer", node.getNodeType() == ASTNode.INITIALIZER);
		Initializer declaration = (Initializer) node;
		ASTParser parser = ASTParser.newParser(AST.JLS2);
		parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
		parser.setSource(source);
		parser.setSourceRange(declaration.getStartPosition(), declaration.getLength());
		parser.setCompilerOptions(JavaScriptCore.getOptions());
		ASTNode result2 = parser.createAST(null);
		assertNotNull("No node", result2);
		assertTrue("not a type declaration", result2.getNodeType() == ASTNode.TYPE_DECLARATION);
		TypeDeclaration typeDeclaration = (TypeDeclaration) result2;
		List bodyDeclarations = typeDeclaration.bodyDeclarations();
		assertEquals("wrong size", 1, bodyDeclarations.size());
		BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
		assertTrue(declaration.subtreeMatch(new ASTMatcher(), bodyDeclaration));
		ASTNode root = bodyDeclaration.getRoot();
		assertNotNull("No root", root);
		assertTrue("not a compilation unit", root.getNodeType() == ASTNode.JAVASCRIPT_UNIT);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) root;
		assertEquals("wrong problem size", 0, compilationUnit.getProblems().length);
		assertNotNull("No comments", compilationUnit.getCommentList());
		assertEquals("Wrong size", 3, compilationUnit.getCommentList().size());
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=48489
	 * @deprecated using deprecated code
	 */
	public void test0519() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0519", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, false);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		assertNotNull("No comments", unit.getCommentList());
		assertEquals("Wrong size", 2, unit.getCommentList().size());
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertNotNull("No node", node);
		ASTNode statement = node;
		ASTParser parser = ASTParser.newParser(AST.JLS2);
		parser.setKind(ASTParser.K_STATEMENTS);
		parser.setSource(source);
		parser.setSourceRange(statement.getStartPosition(), statement.getLength());
		parser.setCompilerOptions(JavaScriptCore.getOptions());
		ASTNode result2 = parser.createAST(null);
		assertNotNull("No node", result2);
		assertTrue("not a block", result2.getNodeType() == ASTNode.BLOCK);
		Block block = (Block) result2;
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size());
		Statement statement2 = (Statement) statements.get(0);
		assertTrue(statement.subtreeMatch(new ASTMatcher(), statement2));
		ASTNode root = statement2.getRoot();
		assertNotNull("No root", root);
		assertTrue("not a compilation unit", root.getNodeType() == ASTNode.JAVASCRIPT_UNIT);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) root;
		assertEquals("wrong problem size", 0, compilationUnit.getProblems().length);
		assertNotNull("No comments", compilationUnit.getCommentList());
		assertEquals("Wrong size", 1, compilationUnit.getCommentList().size());
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=48489
	 * @deprecated using deprecated code
	 */
	public void test0520() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0520", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, false);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		assertNotNull("No comments", unit.getCommentList());
		assertEquals("Wrong size", 2, unit.getCommentList().size());
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a block", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		ASTParser parser = ASTParser.newParser(AST.JLS2);
		parser.setKind(ASTParser.K_EXPRESSION);
		parser.setSource(source);
		parser.setSourceRange(expression.getStartPosition(), expression.getLength());
		parser.setCompilerOptions(JavaScriptCore.getOptions());
		ASTNode result2 = parser.createAST(null);
		assertNotNull("No node", result2);
		assertTrue("not a method invocation", result2.getNodeType() == ASTNode.FUNCTION_INVOCATION);
		assertTrue(expression.subtreeMatch(new ASTMatcher(), result2));
		ASTNode root = result2.getRoot();
		assertNotNull("No root", root);
		assertTrue("not a compilation unit", root.getNodeType() == ASTNode.JAVASCRIPT_UNIT);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) root;
		assertEquals("wrong problem size", 0, compilationUnit.getProblems().length);
		assertNotNull("No comments", compilationUnit.getCommentList());
		assertEquals("Wrong size", 1, compilationUnit.getCommentList().size());
	}
	/**
	 * Ensure an OperationCanceledException is correcly thrown when progress monitor is canceled
	 * @deprecated using deprecated code
	 */
	public void test0521() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0521", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
		// count the number of time isCanceled() is called when converting this source unit
		WorkingCopyOwner owner = new WorkingCopyOwner() {};
		CancelCounter counter = new CancelCounter();
		ASTParser parser = ASTParser.newParser(AST.JLS2);
		parser.setSource(sourceUnit);
		parser.setResolveBindings(true);
		parser.setWorkingCopyOwner(owner);
		parser.createAST(counter);
		
		// throw an OperatonCanceledException at each point isCanceled() is called
		for (int i = 0; i < counter.count; i++) {
			boolean gotException = false;
			try {
				parser = ASTParser.newParser(AST.JLS2);
				parser.setSource(sourceUnit);
				parser.setResolveBindings(true);
				parser.setWorkingCopyOwner(owner);
				parser.createAST(new Canceler(i));
			} catch (OperationCanceledException e) {
				gotException = true;
			}
			assertTrue("Should get an OperationCanceledException (" + i + ")", gotException);
		}
		
		// last should not throw an OperationCanceledException
		parser = ASTParser.newParser(AST.JLS2);
		parser.setSource(sourceUnit);
		parser.setResolveBindings(true);
		parser.setWorkingCopyOwner(owner);
		parser.createAST(new Canceler(counter.count));
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48292
	 */
	public void test0522() throws JavaScriptModelException {
		IClassFile classFile = getClassFile("Converter" , "bins", "test0522", "Test.class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertNotNull(classFile);
		assertNotNull(classFile.getSource());
		IType type = classFile.getType();
		assertNotNull(type);
		IFunction[] methods = type.getFunctions();
		assertNotNull(methods);
		assertEquals("wrong size", 2, methods.length);
		IFunction method = methods[1];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(classFile, sourceRange.getOffset() + sourceRange.getLength() / 2, true);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 1, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "test", methodDeclaration.getName().getIdentifier());
		IFunctionBinding methodBinding = methodDeclaration.resolveBinding();
		assertNotNull(methodBinding);
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 2, statements.size());
		ASTNode node2 = (ASTNode) statements.get(1);
		assertNotNull(node2);
		assertTrue("Not an expression statement", node2.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node2;
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		assertTrue("Not a simple name", expression2.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) expression2;
		IBinding binding  = simpleName.resolveBinding();
		assertNotNull("No binding", binding); //$NON-NLS-1$
		assertTrue("wrong type", binding.getKind() == IBinding.VARIABLE); //$NON-NLS-1$
		IVariableBinding variableBinding = (IVariableBinding) binding;
		assertEquals("Wrong name", "a", variableBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		SimpleName simpleName2 = methodInvocation.getName();
		assertEquals("Wrong name", "clone", simpleName2.getIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
		IBinding binding2 = simpleName2.resolveBinding();
		assertNotNull("no binding2", binding2); //$NON-NLS-1$
		assertTrue("Wrong type", binding2.getKind() == IBinding.METHOD); //$NON-NLS-1$
		IFunctionBinding methodBinding2 = (IFunctionBinding) binding2;
		assertEquals("Wrong name", "clone", methodBinding2.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48292
	 */
	public void test0523() throws JavaScriptModelException {
		IClassFile classFile = getClassFile("Converter" , "bins", "test0523", "Test.class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertNotNull(classFile);
		assertNotNull(classFile.getSource());
		IType type = classFile.getType();
		assertNotNull(type);
		IFunction[] methods = type.getFunctions();
		assertNotNull(methods);
		assertEquals("wrong size", 2, methods.length);
		IFunction method = methods[1];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(classFile, sourceRange.getOffset() + sourceRange.getLength() / 2, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 1, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "test", methodDeclaration.getName().getIdentifier());
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 2, statements.size());
		ASTNode node2 = (ASTNode) statements.get(1);
		assertNotNull(node2);
		assertTrue("Not an expression statement", node2.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) node2;
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		assertTrue("Not a simple name", expression2.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) expression2;
		IBinding binding  = simpleName.resolveBinding();
		assertNull("No binding", binding); //$NON-NLS-1$
		SimpleName simpleName2 = methodInvocation.getName();
		assertEquals("Wrong name", "clone", simpleName2.getIdentifier()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48292
	 */
	public void test0524() throws JavaScriptModelException {
		IClassFile classFile = getClassFile("Converter" , "bins", "test0524", "A.class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertNotNull(classFile);
		assertNotNull(classFile.getSource());
		IType type = classFile.getType();
		assertNotNull(type);
		IFunction[] methods = type.getFunctions();
		assertNotNull(methods);
		assertEquals("wrong size", 2, methods.length);
		IFunction method = methods[1];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(classFile, sourceRange.getOffset() + sourceRange.getLength() / 2, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "test", methodDeclaration.getName().getIdentifier());
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 1, statements.size());
		ASTNode node2 = (ASTNode) statements.get(0);
		assertNotNull(node2);
		assertTrue("Not an variable declaration statement", node2.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48292
	 */
	public void test0525() throws JavaScriptModelException {
		IClassFile classFile = getClassFile("Converter" , "bins", "test0525", "A.class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertNotNull(classFile);
		assertNotNull(classFile.getSource());
		IType type = classFile.getType();
		assertNotNull(type);
		IFunction[] methods = type.getFunctions();
		assertNotNull(methods);
		assertEquals("wrong size", 2, methods.length);
		IFunction method = methods[1];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(classFile, sourceRange.getOffset() + sourceRange.getLength() / 2, true);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "test", methodDeclaration.getName().getIdentifier());
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 1, statements.size());
		ASTNode node2 = (ASTNode) statements.get(0);
		assertNotNull(node2);
		assertTrue("Not an variable declaration statement", node2.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node2;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertTrue("Not a class instance creation", expression.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
		ITypeBinding typeBinding = classInstanceCreation.resolveTypeBinding();
		assertNotNull(typeBinding);
	}
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48292
	 */
	public void test0526() throws JavaScriptModelException {
		IClassFile classFile = getClassFile("Converter" , "bins", "test0526", "A.class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertNotNull(classFile);
		assertNotNull(classFile.getSource());
		IType type = classFile.getType();
		assertNotNull(type);
		IType[] memberTypes = type.getTypes();
		assertNotNull(memberTypes);
		assertEquals("wrong size", 1, memberTypes.length);
		IType memberType = memberTypes[0];
		IFunction[] methods = memberType.getFunctions();
		assertEquals("wrong size", 2, methods.length);
		IFunction method = methods[1];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(classFile, sourceRange.getOffset() + sourceRange.getLength() / 2, true);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "test", methodDeclaration.getName().getIdentifier());
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 1, statements.size());
		ASTNode node2 = (ASTNode) statements.get(0);
		assertNotNull(node2);
		assertTrue("Not an variable declaration statement", node2.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node2;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertTrue("Not a class instance creation", expression.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
		ITypeBinding typeBinding = classInstanceCreation.resolveTypeBinding();
		assertNotNull(typeBinding);
		assertTrue(typeBinding.isAnonymous());
		assertEquals("Wrong name", "", typeBinding.getName());
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48292
	 */
	public void test0527() throws JavaScriptModelException {
		IClassFile classFile = getClassFile("Converter" , "bins", "test0527", "A.class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertNotNull(classFile);
		assertNotNull(classFile.getSource());
		IType type = classFile.getType();
		assertNotNull(type);
		IFunction[] methods = type.getFunctions();
		assertEquals("wrong size", 1, methods.length);
		IFunction method = methods[0];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(classFile, sourceRange.getOffset() + sourceRange.getLength() / 2, true);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "A", methodDeclaration.getName().getIdentifier());
		assertTrue("Not a constructor", methodDeclaration.isConstructor());
		IBinding binding = methodDeclaration.getName().resolveBinding();
		assertNotNull(binding);
		assertEquals("Wrong type", IBinding.METHOD, binding.getKind());
		List statements = ((FunctionDeclaration) node).getBody().statements();
		assertEquals("wrong size", 1, statements.size());
		ASTNode node2 = (ASTNode) statements.get(0);
		assertNotNull(node2);
		assertTrue("Not an variable declaration statement", node2.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement statement = (VariableDeclarationStatement) node2;
		List fragments = statement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertTrue("Not a class instance creation", expression.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
		ITypeBinding typeBinding = classInstanceCreation.resolveTypeBinding();
		assertNotNull(typeBinding);
		assertTrue(typeBinding.isAnonymous());
		assertEquals("Wrong name", "", typeBinding.getName());
	}
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48292
	 */
	public void test0528() throws JavaScriptModelException {
		IClassFile classFile = getClassFile("Converter" , "bins", "test0528", "A.class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertNotNull(classFile);
		assertNotNull(classFile.getSource());
		IType type = classFile.getType();
		IType[] memberTypes = type.getTypes();
		assertNotNull(memberTypes);
		assertEquals("wrong size", 1, memberTypes.length);
		IType memberType = memberTypes[0];
		ISourceRange sourceRange = memberType.getSourceRange(); 
		ASTNode result = runConversion(classFile, sourceRange.getOffset() + sourceRange.getLength() / 2, true);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION); //$NON-NLS-1$
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		assertEquals("wrong name", "B", typeDeclaration.getName().getIdentifier());
		List bodyDeclarations = typeDeclaration.bodyDeclarations();
		assertEquals("Wrong size", 1, bodyDeclarations.size());
		BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
		assertTrue("Not a method declaration", bodyDeclaration.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) bodyDeclaration;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("Wrong size", 1, statements.size());
	}
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48292
	 */
	public void test0529() throws JavaScriptModelException {
		IClassFile classFile = getClassFile("Converter" , "bins", "test0529", "A.class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertNotNull(classFile);
		assertNotNull(classFile.getSource());
		IType type = classFile.getType();
		IType[] memberTypes = type.getTypes();
		assertNotNull(memberTypes);
		assertEquals("wrong size", 1, memberTypes.length);
		IType memberType = memberTypes[0];
		ISourceRange sourceRange = memberType.getSourceRange(); 
		ASTNode result = runConversion(classFile, sourceRange.getOffset() + sourceRange.getLength() / 2, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION); //$NON-NLS-1$
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		assertEquals("wrong name", "B", typeDeclaration.getName().getIdentifier());
		List bodyDeclarations = typeDeclaration.bodyDeclarations();
		assertEquals("Wrong size", 1, bodyDeclarations.size());
		BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
		assertTrue("Not a method declaration", bodyDeclaration.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) bodyDeclaration;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("Wrong size", 1, statements.size());
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48292
	 */
	public void test0530() throws JavaScriptModelException {
		IClassFile classFile = getClassFile("Converter" , "bins", "test0530", "A.class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertNotNull(classFile);
		assertNotNull(classFile.getSource());
		IType type = classFile.getType();
		IFunction[] methods = type.getFunctions();
		assertEquals("wrong size", 3, methods.length);
		IFunction method = methods[2];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(classFile, sourceRange.getOffset() + sourceRange.getLength() / 2, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 2);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("Wrong size", 2, statements.size());

		node = getASTNode((JavaScriptUnit) result, 0, 1);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		methodDeclaration = (FunctionDeclaration) node;
		block = methodDeclaration.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
	}
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48292
	 */
	public void test0531() throws JavaScriptModelException {
		IClassFile classFile = getClassFile("Converter" , "bins", "test0531", "A.class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertNotNull(classFile);
		assertNotNull(classFile.getSource());
		IType type = classFile.getType();
		IFunction[] methods = type.getFunctions();
		assertEquals("wrong size", 5, methods.length);
		IFunction method = methods[3];
		ISourceRange sourceRange = method.getSourceRange(); 
		ASTNode result = runConversion(classFile, sourceRange.getOffset() + sourceRange.getLength() / 2, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$

		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 5);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("Wrong size", 2, statements.size());

		node = getASTNode((JavaScriptUnit) result, 0, 4);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		methodDeclaration = (FunctionDeclaration) node;
		block = methodDeclaration.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		List fragments = fieldDeclaration.fragments();
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Wrong name", "field", fragment.getName().getIdentifier());
		assertNotNull("No initializer", expression);

		node = getASTNode((JavaScriptUnit) result, 0, 1);
		assertTrue("Not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		fragment = (VariableDeclarationFragment) fragments.get(0);
		expression = fragment.getInitializer();
		assertEquals("Wrong name", "i", fragment.getName().getIdentifier());
		assertNotNull("No initializer", expression);

		node = getASTNode((JavaScriptUnit) result, 0, 2);
		assertTrue("Not an initializer", node.getNodeType() == ASTNode.INITIALIZER); //$NON-NLS-1$
		Initializer initializer = (Initializer) node;
		assertEquals("Not static", Modifier.NONE, initializer.getModifiers());
		block = initializer.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 3);
		assertTrue("Not an initializer", node.getNodeType() == ASTNode.INITIALIZER); //$NON-NLS-1$
		initializer = (Initializer) node;
		assertEquals("Not static", Modifier.STATIC, initializer.getModifiers());
		block = initializer.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 6);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		methodDeclaration = (FunctionDeclaration) node;
		block = methodDeclaration.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
	}

	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48292
	 */
	public void test0532() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter" , "src", "test0488", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		IType[] types = sourceUnit.getTypes();
		assertNotNull(types);
		assertEquals("wrong size", 1, types.length);
		IType type = types[0];
		IInitializer[] initializers = type.getInitializers();
		assertEquals("wrong size", 2, initializers.length);
		IInitializer init = initializers[1];
		ISourceRange sourceRange = init.getSourceRange(); 
		int position = sourceRange.getOffset() + sourceRange.getLength() / 2;

		IClassFile classFile = getClassFile("Converter" , "bins", "test0532", "A.class"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertNotNull(classFile);
		assertNotNull(classFile.getSource());
		type = classFile.getType();
		initializers = type.getInitializers();
		assertEquals("wrong size", 0, initializers.length);
		ASTNode result = runConversion(classFile, position, false);
		assertNotNull(result);
		assertTrue("Not a compilation unit", result.getNodeType() == ASTNode.JAVASCRIPT_UNIT); //$NON-NLS-1$
	
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 5);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
	
		node = getASTNode((JavaScriptUnit) result, 0, 4);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		methodDeclaration = (FunctionDeclaration) node;
		block = methodDeclaration.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertTrue("Not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		List fragments = fieldDeclaration.fragments();
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		Expression expression = fragment.getInitializer();
		assertEquals("Wrong name", "field", fragment.getName().getIdentifier());
		assertNotNull("No initializer", expression);
	
		node = getASTNode((JavaScriptUnit) result, 0, 1);
		assertTrue("Not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		fragment = (VariableDeclarationFragment) fragments.get(0);
		expression = fragment.getInitializer();
		assertEquals("Wrong name", "i", fragment.getName().getIdentifier());
		assertNotNull("No initializer", expression);
	
		node = getASTNode((JavaScriptUnit) result, 0, 2);
		assertTrue("Not an initializer", node.getNodeType() == ASTNode.INITIALIZER); //$NON-NLS-1$
		Initializer initializer = (Initializer) node;
		assertEquals("Not static", Modifier.NONE, initializer.getModifiers());
		block = initializer.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 3);
		assertTrue("Not an initializer", node.getNodeType() == ASTNode.INITIALIZER); //$NON-NLS-1$
		initializer = (Initializer) node;
		assertEquals("Not static", Modifier.STATIC, initializer.getModifiers());
		block = initializer.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 1, statements.size());
		
		node = getASTNode((JavaScriptUnit) result, 0, 6);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		methodDeclaration = (FunctionDeclaration) node;
		block = methodDeclaration.getBody();
		statements = block.statements();
		assertEquals("Wrong size", 0, statements.size());
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=48489
	 * @deprecated using deprecated code
	 */
	public void test0533() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0533", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, false);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION);
		FunctionDeclaration declaration = (FunctionDeclaration) node;
		ASTParser parser = ASTParser.newParser(AST.JLS2);
		parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
		parser.setSource(source);
		parser.setSourceRange(declaration.getStartPosition(), declaration.getLength());
		parser.setCompilerOptions(JavaScriptCore.getOptions());
		ASTNode result2 = parser.createAST(null);
		assertNotNull("No node", result2);
		assertTrue("not a compilation unit", result2.getNodeType() == ASTNode.JAVASCRIPT_UNIT);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result2;
		assertEquals("wrong problem size", 1, compilationUnit.getProblems().length);
	}	
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=48489
	 * @deprecated using deprecated code
	 */
	public void test0534() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0534", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, false);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION);
		FieldDeclaration declaration = (FieldDeclaration) node;
		ASTParser parser = ASTParser.newParser(AST.JLS2);
		parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
		parser.setSource(source);
		parser.setSourceRange(declaration.getStartPosition(), declaration.getLength());
		parser.setCompilerOptions(JavaScriptCore.getOptions());
		ASTNode result2 = parser.createAST(null);
		assertNotNull("No node", result2);
		assertTrue("not a compilation unit", result2.getNodeType() == ASTNode.JAVASCRIPT_UNIT);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result2;
		assertEquals("wrong problem size", 1, compilationUnit.getProblems().length);
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=48489
	 * @deprecated using deprecated code
	 */
	public void test0535() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0535", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, false);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertNotNull("No node", node);
		assertTrue("not an initializer", node.getNodeType() == ASTNode.INITIALIZER);
		Initializer declaration = (Initializer) node;
		ASTParser parser = ASTParser.newParser(AST.JLS2);
		parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
		parser.setSource(source);
		parser.setSourceRange(declaration.getStartPosition(), declaration.getLength());
		parser.setCompilerOptions(JavaScriptCore.getOptions());
		ASTNode result2 = parser.createAST(null);
		assertNotNull("No node", result2);
		assertTrue("not a compilation unit", result2.getNodeType() == ASTNode.JAVASCRIPT_UNIT);
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result2;
		assertEquals("wrong problem size", 1, compilationUnit.getProblems().length);
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=47396
	 */
	public void test0536() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0536", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, false);
		assertNotNull("No compilation unit", result);
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=51089
	 */
	public void test0537a() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0537", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, false);
		assertNotNull("No compilation unit", result);
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=51089
	 */
	public void test0537b() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0537", "B.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, false);
		assertNotNull("No compilation unit", result);
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=51089
	 */
	public void test0537c() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0537", "C.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, false);
		assertNotNull("No compilation unit", result);
	}
	/**
	 * Ensures that an AST can be created during reconcile.
	 * @deprecated using deprecated code
	 */
	public void test0538a() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0538", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		try {
			sourceUnit.becomeWorkingCopy(null, null);
			sourceUnit.getBuffer().setContents(
				"package test0538;\n" +
				"public class A {\n" +
				"  int i;\n" +
				"}"
			);
			JavaScriptUnit unit = sourceUnit.reconcile(AST.JLS2, false, null, null);
			assertNotNull("No level 2 compilation unit", unit);
			assertEquals("Compilation unit has wrong AST level (2)", AST.JLS2, unit.getAST().apiLevel());
			// TODO improve test for AST.JLS3
		} finally {
			sourceUnit.discardWorkingCopy();
		}
	}
	/*
	 * Ensures that no AST is created during reconcile if not requested.
	 */
	public void test0538b() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0538", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		try {
			sourceUnit.becomeWorkingCopy(null, null);
			sourceUnit.getBuffer().setContents(
				"package test0538;\n" +
				"public class A {\n" +
				"  int i;\n" +
				"}"
			);
			JavaScriptUnit unit = sourceUnit.reconcile(0, false, null, null);
			assertNull("Unexpected compilation unit", unit);
		} finally {
			sourceUnit.discardWorkingCopy();
		}
	}
	/**
	 * Ensures that no AST is created during reconcile if consistent.
	 * @deprecated using deprecated code
	 */
	public void test0538c() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0538", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		try {
			sourceUnit.becomeWorkingCopy(null, null);
			JavaScriptUnit unit = sourceUnit.reconcile(AST.JLS2, false, null, null);
			assertNull("Unexpected compilation unit", unit);
			// TODO improve test for AST.JLS3
		} finally {
			sourceUnit.discardWorkingCopy();
		}
	}
	/**
	 * Ensures that bindings are created during reconcile if the problem requestor is active.
	 * @deprecated using deprecated code
	 */
	public void test0538d() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0538", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		try {
			ReconcilerTests.ProblemRequestor pbRequestor = new ReconcilerTests.ProblemRequestor();
			sourceUnit.becomeWorkingCopy(pbRequestor, null);
			sourceUnit.getBuffer().setContents(
				"package test0538;\n" +
				"public class A {\n" +
				"  Object field;\n" +
				"}"
			);
			// TODO improve test for AST.JLS3
			JavaScriptUnit unit = sourceUnit.reconcile(AST.JLS2, false, null, null);
			ASTNode node = getASTNode(unit, 0, 0);
			assertNotNull("No node", node);
			assertTrue("Not original", isOriginal(node));
			assertTrue("not a field declaration", node.getNodeType() == ASTNode.FIELD_DECLARATION);
			FieldDeclaration declaration = (FieldDeclaration) node;
			Type type = declaration.getType();
			ITypeBinding typeBinding = type.resolveBinding();
			assertNotNull("No type binding", typeBinding); 
			assertEquals("Wrong name", "Object", typeBinding.getName());
		} finally {
			sourceUnit.discardWorkingCopy();
		}
	}
	/**
	 * Ensures that bindings are created during reconcile if force problem detection is turned on.
	 * @deprecated using deprecated code
	 */
	public void test0538e() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0538", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		try {
			ReconcilerTests.ProblemRequestor pbRequestor = new ReconcilerTests.ProblemRequestor();
			sourceUnit.becomeWorkingCopy(pbRequestor, null);
			// TODO improve test for AST.JLS3
			JavaScriptUnit unit = sourceUnit.reconcile(AST.JLS2, true/*force pb detection*/, null, null);
			ASTNode node = getASTNode(unit, 0);
			assertNotNull("No node", node);
			assertTrue("not a type declaration", node.getNodeType() == ASTNode.TYPE_DECLARATION);
			TypeDeclaration declaration = (TypeDeclaration) node;
			ITypeBinding typeBinding = declaration.resolveBinding();
			assertNotNull("No type binding", typeBinding); 
			assertEquals("Wrong name", "A", typeBinding.getName());
		} finally {
			sourceUnit.discardWorkingCopy();
		}
	}
	/**
	 * Ensures that bindings are created during reconcile if force problem detection is turned on.
	 * Case of a unit containing an anonymous type.
	 * (regression test for bug 55102 NPE when using ICU.reconcile(GET_AST_TRUE, ...))
	 * @deprecated using deprecated code
	 */
	public void test0538f() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0538", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		try {
			ReconcilerTests.ProblemRequestor pbRequestor = new ReconcilerTests.ProblemRequestor();
			sourceUnit.becomeWorkingCopy(pbRequestor, null);
			sourceUnit.getBuffer().setContents(
				"package test0538;\n" +
				"public class A {\n" +
				"  void foo() {\n" +
				"    new Object() {\n" +
				"      void bar() {\n" +
				"      }\n" +
				"    };\n" +
				"  }\n" +
				"}"
			);
			// TODO improve test for AST.JLS3
			JavaScriptUnit unit = sourceUnit.reconcile(AST.JLS2, true/*force pb detection*/, null, null);
			ASTNode node = getASTNode(unit, 0);
			assertNotNull("No node", node);
		} finally {
			sourceUnit.discardWorkingCopy();
		}
	}
	/**
	 * Ensures that bindings are created during reconcile if force problem detection is turned on.
	 * Case of a unit containing an anonymous type.
	 * (regression test for bug 55102 NPE when using ICU.reconcile(GET_AST_TRUE, ...))
	 * @deprecated using deprecated code
	 */
	public void test0538g() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0538", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		try {
			ReconcilerTests.ProblemRequestor pbRequestor = new ReconcilerTests.ProblemRequestor();
			sourceUnit.becomeWorkingCopy(pbRequestor, null);
			sourceUnit.getBuffer().setContents(
				"package test0538;\n" +
				"public class A {\n" +
				"  void foo() {\n" +
				"    new Object() {\n" +
				"      void bar() {\n" +
				"      }\n" +
				"    };\n" +
				"  }\n" +
				"}"
			);
			sourceUnit.reconcile(IJavaScriptUnit.NO_AST, false/* don't force pb detection*/, null, null);
			// TODO improve test for AST.JLS3
			JavaScriptUnit unit = sourceUnit.reconcile(AST.JLS2, true/*force pb detection*/, null, null);
			ASTNode node = getASTNode(unit, 0);
			assertNotNull("No node", node);
		} finally {
			sourceUnit.discardWorkingCopy();
		}
	}
	/**
	 * Ensures that asking for well known type doesn't throw a NPE if the problem requestor is not active.
	 * (regression test for bug 64750 NPE in Java AST Creation - editing some random file)
	 * @deprecated using deprecated code
	 */
	public void test0538h() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0538", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		try {
			ReconcilerTests.ProblemRequestor pbRequestor = new ReconcilerTests.ProblemRequestor() {
                public boolean isActive() {
                    return false;
                }
			};
			sourceUnit.becomeWorkingCopy(pbRequestor, null);
			sourceUnit.getBuffer().setContents(
				"package test0538;\n" +
				"public class A {\n" +
				"  Object field;\n" +
				"}"
			);
			// TODO improve test for AST.JLS3
			JavaScriptUnit unit = sourceUnit.reconcile(AST.JLS2, false, null, null);
			assertEquals("Unexpected well known type", null, unit.getAST().resolveWellKnownType("void"));
		} finally {
			sourceUnit.discardWorkingCopy();
		}
	}
	/**
	 * http://dev.eclipse.org/bugs/show_bug.cgi?id=53477
	 */
	public void test0539() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0539", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, false);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 1, 0);
		assertNotNull("No node", node);
		assertTrue("not an expression statement", node.getNodeType() == ASTNode.EXPRESSION_STATEMENT);
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertTrue("not a class instance creation", expression.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION);
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) expression;
		checkSourceRange(classInstanceCreation, "new A(){}.new Inner(){/*x*/}", source);
		AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
		Expression expression2 = classInstanceCreation.getExpression();
		assertTrue("not a class instance creation", expression2.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION);
		ClassInstanceCreation classInstanceCreation2 = (ClassInstanceCreation) expression2;
		AnonymousClassDeclaration anonymousClassDeclaration2 = classInstanceCreation2.getAnonymousClassDeclaration();
		assertNotNull("No anonymous class declaration", anonymousClassDeclaration2);
		checkSourceRange(anonymousClassDeclaration2, "{}", source);
		assertNotNull("No anonymous class declaration", anonymousClassDeclaration);
		checkSourceRange(anonymousClassDeclaration, "{/*x*/}", source);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=54431
	 */
	public void test0540() {
		char[] source = 
				("package test0540;\n" +  //$NON-NLS-1$
				"\n" +  //$NON-NLS-1$
				"class Test {\n" +  //$NON-NLS-1$
				"	public void foo(int arg) {\n" +//$NON-NLS-1$
				"		assert true;\n" +//$NON-NLS-1$
				"	}\n" +  //$NON-NLS-1$
				"}").toCharArray(); //$NON-NLS-1$
		IJavaScriptProject project = getJavaProject("Converter"); //$NON-NLS-1$
		Map options = project.getOptions(true);
		options.put(JavaScriptCore.COMPILER_SOURCE, JavaScriptCore.VERSION_1_4);
		options.put(JavaScriptCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaScriptCore.VERSION_1_4);
		options.put(JavaScriptCore.COMPILER_COMPLIANCE, JavaScriptCore.VERSION_1_4);
		ASTNode result = runConversion(source, "Test.js", project, options, true); //$NON-NLS-1$
		assertNotNull("No compilation unit", result); //$NON-NLS-1$
		assertTrue("result is not a compilation unit", result instanceof JavaScriptUnit); //$NON-NLS-1$
		JavaScriptUnit compilationUnit = (JavaScriptUnit) result;
		assertEquals("Problems found", 0, compilationUnit.getProblems().length);
		ASTNode node = getASTNode(compilationUnit, 0);
		assertTrue("not a TypeDeclaration", node instanceof TypeDeclaration); //$NON-NLS-1$
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		ITypeBinding typeBinding = typeDeclaration.resolveBinding();
		assertNotNull("No type binding", typeBinding); //$NON-NLS-1$
		assertEquals("Wrong name", "Test", typeBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Wrong package", "test0540", typeBinding.getPackage().getName()); //$NON-NLS-1$ //$NON-NLS-2$
		assertTrue("Not an interface", typeBinding.isClass()); //$NON-NLS-1$
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=56697
	 */
	public void test0541() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0541", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertEquals("not a field declaration", ASTNode.FIELD_DECLARATION, node.getNodeType());
		class Change14FieldAccessASTVisitor extends ASTVisitor {
			int counter;
			Change14FieldAccessASTVisitor() {
				counter = 0;
			}
			public void endVisit(QualifiedName qualifiedName) {
				IBinding i_binding = qualifiedName.getQualifier().resolveBinding();						
				ITypeBinding type_binding = qualifiedName.getQualifier().resolveTypeBinding();
				if (i_binding == null || type_binding == null) {
					counter++;
				}
			}
		}
		Change14FieldAccessASTVisitor visitor = new Change14FieldAccessASTVisitor();
		unit.accept(visitor);
		assertEquals("Missing binding", 0, visitor.counter);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=55004
	 */
	public void test0542() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0542", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertTrue("not a field declaration", node instanceof FieldDeclaration); //$NON-NLS-1$
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		List fragments = fieldDeclaration.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		IVariableBinding variableBinding = fragment.resolveBinding();
		assertNotNull("No binding", variableBinding);
		assertEquals("Wrong name", "STRING_FIELD", variableBinding.getName());
		Object constantValue = variableBinding.getConstantValue();
		assertNotNull("No constant", constantValue);
		assertEquals("Wrong value", "Hello world!", constantValue);
		Expression initializer = fragment.getInitializer();
		assertNotNull("No initializer", initializer);
		checkSourceRange(initializer, "\"Hello world!\"", source);

		node = getASTNode(unit, 0, 1);
		assertTrue("not a field declaration", node instanceof FieldDeclaration); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		fragment = (VariableDeclarationFragment) fragments.get(0);
		variableBinding = fragment.resolveBinding();
		assertNotNull("No binding", variableBinding);
		assertEquals("Wrong name", "BOOLEAN_FIELD", variableBinding.getName());
		constantValue = variableBinding.getConstantValue();
		assertNotNull("No constant", constantValue);
		assertEquals("Wrong value", new Boolean(true), constantValue);
		initializer = fragment.getInitializer();
		assertNotNull("No initializer", initializer);
		checkSourceRange(initializer, "true", source);

		node = getASTNode(unit, 0, 2);
		assertTrue("not a field declaration", node instanceof FieldDeclaration); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		fragment = (VariableDeclarationFragment) fragments.get(0);
		variableBinding = fragment.resolveBinding();
		assertNotNull("No binding", variableBinding);
		assertEquals("Wrong name", "BYTE_FIELD", variableBinding.getName());
		constantValue = variableBinding.getConstantValue();
		assertNotNull("No constant", constantValue);
		assertEquals("Wrong value", new Byte((byte)1), constantValue);
		initializer = fragment.getInitializer();
		assertNotNull("No initializer", initializer);
		checkSourceRange(initializer, "1", source);

		node = getASTNode(unit, 0, 3);
		assertTrue("not a field declaration", node instanceof FieldDeclaration); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		fragment = (VariableDeclarationFragment) fragments.get(0);
		variableBinding = fragment.resolveBinding();
		assertNotNull("No binding", variableBinding);
		assertEquals("Wrong name", "CHAR_FIELD", variableBinding.getName());
		constantValue = variableBinding.getConstantValue();
		assertNotNull("No constant", constantValue);
		assertEquals("Wrong value", new Character('{'), constantValue);
		initializer = fragment.getInitializer();
		assertNotNull("No initializer", initializer);
		checkSourceRange(initializer, "\'{\'", source);

		node = getASTNode(unit, 0, 4);
		assertTrue("not a field declaration", node instanceof FieldDeclaration); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		fragment = (VariableDeclarationFragment) fragments.get(0);
		variableBinding = fragment.resolveBinding();
		assertNotNull("No binding", variableBinding);
		assertEquals("Wrong name", "DOUBLE_FIELD", variableBinding.getName());
		constantValue = variableBinding.getConstantValue();
		assertNotNull("No constant", constantValue);
		assertEquals("Wrong value", new Double("3.1415"), constantValue);
		initializer = fragment.getInitializer();
		assertNotNull("No initializer", initializer);
		checkSourceRange(initializer, "3.1415", source);
		
		node = getASTNode(unit, 0, 5);
		assertTrue("not a field declaration", node instanceof FieldDeclaration); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		fragment = (VariableDeclarationFragment) fragments.get(0);
		variableBinding = fragment.resolveBinding();
		assertNotNull("No binding", variableBinding);
		assertEquals("Wrong name", "FLOAT_FIELD", variableBinding.getName());
		constantValue = variableBinding.getConstantValue();
		assertNotNull("No constant", constantValue);
		assertEquals("Wrong value", new Float("3.14159f"), constantValue);
		initializer = fragment.getInitializer();
		assertNotNull("No initializer", initializer);
		checkSourceRange(initializer, "3.14159f", source);

		node = getASTNode(unit, 0, 6);
		assertTrue("not a field declaration", node instanceof FieldDeclaration); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		fragment = (VariableDeclarationFragment) fragments.get(0);
		variableBinding = fragment.resolveBinding();
		assertNotNull("No binding", variableBinding);
		assertEquals("Wrong name", "INT_FIELD", variableBinding.getName());
		constantValue = variableBinding.getConstantValue();
		assertNotNull("No constant", constantValue);
		assertEquals("Wrong value", Integer.valueOf("7fffffff", 16), constantValue);
		initializer = fragment.getInitializer();
		assertNotNull("No initializer", initializer);
		checkSourceRange(initializer, "Integer.MAX_VALUE", source);
		
		node = getASTNode(unit, 0, 7);
		assertTrue("not a field declaration", node instanceof FieldDeclaration); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		fragment = (VariableDeclarationFragment) fragments.get(0);
		variableBinding = fragment.resolveBinding();
		assertNotNull("No binding", variableBinding);
		assertEquals("Wrong name", "LONG_FIELD", variableBinding.getName());
		constantValue = variableBinding.getConstantValue();
		assertNotNull("No constant", constantValue);
		assertEquals("Wrong value", new Long("34"), constantValue);
		initializer = fragment.getInitializer();
		assertNotNull("No initializer", initializer);
		checkSourceRange(initializer, "34L", source);
		
		node = getASTNode(unit, 0, 8);
		assertTrue("not a field declaration", node instanceof FieldDeclaration); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		fragment = (VariableDeclarationFragment) fragments.get(0);
		variableBinding = fragment.resolveBinding();
		assertNotNull("No binding", variableBinding);
		assertEquals("Wrong name", "SHORT_FIELD", variableBinding.getName());
		constantValue = variableBinding.getConstantValue();
		assertNotNull("No constant", constantValue);
		assertEquals("Wrong value", new Short("130"), constantValue);
		initializer = fragment.getInitializer();
		assertNotNull("No initializer", initializer);
		checkSourceRange(initializer, "130", source);
		
		node = getASTNode(unit, 0, 9);
		assertTrue("not a field declaration", node instanceof FieldDeclaration); //$NON-NLS-1$
		fieldDeclaration = (FieldDeclaration) node;
		fragments = fieldDeclaration.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		fragment = (VariableDeclarationFragment) fragments.get(0);
		variableBinding = fragment.resolveBinding();
		assertNotNull("No binding", variableBinding);
		assertEquals("Wrong name", "int_field", variableBinding.getName());
		constantValue = variableBinding.getConstantValue();
		assertNull("Got a constant", constantValue);
		initializer = fragment.getInitializer();
		assertNotNull("No initializer", initializer);
		checkSourceRange(initializer, "Integer.MAX_VALUE", source);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=58436
	 */
	public void test0543() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0543", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		unit.accept(new GetKeyVisitor());
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=51500
	 */
	public void test0544() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0544", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertEquals("not a method declaration", ASTNode.FUNCTION_DECLARATION, node.getNodeType()); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Not an abstract method", (methodDeclaration.getModifiers() & Modifier.ABSTRACT) != 0);
		IFunctionBinding methodBinding = methodDeclaration.resolveBinding();
		assertNotNull("No binding", methodBinding);
		assertTrue("Not an abstract method binding", (methodBinding.getModifiers() & Modifier.ABSTRACT) != 0);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59843
	 */
	public void test0545() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0545", "First.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertEquals("not a type declaration", ASTNode.TYPE_DECLARATION, node.getNodeType()); //$NON-NLS-1$
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		ITypeBinding typeBinding = typeDeclaration.resolveBinding();
		assertEquals("Wrong key", "Ltest0545/First$Test;", typeBinding.getKey());
		
		sourceUnit = getCompilationUnit("Converter", "src", "test0545", "Second.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		result = runConversion(sourceUnit, true);
		unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		node = getASTNode(unit, 0, 0);
		assertEquals("not a method declaration", ASTNode.TYPE_DECLARATION, node.getNodeType()); //$NON-NLS-1$
		typeDeclaration = (TypeDeclaration) node;
		typeBinding = typeDeclaration.resolveBinding();
		assertEquals("Wrong key", "Ltest0545/Second$Test;", typeBinding.getKey());
		
		sourceUnit = getCompilationUnit("Converter", "src", "test0545", "Third.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		result = runConversion(sourceUnit, true);
		unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		node = getASTNode(unit, 0, 0);
		assertEquals("not a type declaration", ASTNode.TYPE_DECLARATION, node.getNodeType()); //$NON-NLS-1$
		typeDeclaration = (TypeDeclaration) node;
		typeBinding = typeDeclaration.resolveBinding();
		assertEquals("Wrong key", "Ltest0545/Third$Test;", typeBinding.getKey());

	
		sourceUnit = getCompilationUnit("Converter", "src", "test0545", "Test.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		result = runConversion(sourceUnit, true);
		unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		node = getASTNode(unit, 0);
		assertEquals("not a type declaration", ASTNode.TYPE_DECLARATION, node.getNodeType()); //$NON-NLS-1$
		typeDeclaration = (TypeDeclaration) node;
		typeBinding = typeDeclaration.resolveBinding();
		assertEquals("Wrong key", "Ltest0545/Test;", typeBinding.getKey());
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59848
	 * @deprecated using deprecated code
	 */
	public void test0546() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0546", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		final JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 0, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 1, 0, 0);
		assertEquals("not a variable declaration", ASTNode.VARIABLE_DECLARATION_STATEMENT, node.getNodeType()); //$NON-NLS-1$
		VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) node;
		List fragments = variableDeclarationStatement.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		IVariableBinding variableBinding = fragment.resolveBinding();
		ITypeBinding typeBinding = variableBinding.getType();
		assertTrue("An anonymous type binding", !typeBinding.isAnonymous());
		Expression initializer = fragment.getInitializer();
		assertEquals("not a class instance creation", ASTNode.CLASS_INSTANCE_CREATION, initializer.getNodeType()); //$NON-NLS-1$
		ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) initializer;
		AnonymousClassDeclaration anonymousClassDeclaration = classInstanceCreation.getAnonymousClassDeclaration();
		ITypeBinding typeBinding2 = anonymousClassDeclaration.resolveBinding();
		assertTrue("Not an anonymous type binding", typeBinding2.isAnonymous());
		ITypeBinding typeBinding3 = classInstanceCreation.resolveTypeBinding();
		assertTrue("Not an anonymous type binding", typeBinding3.isAnonymous());
		node = getASTNode(unit, 1, 0, 1);
		assertEquals("not a expression statement", ASTNode.EXPRESSION_STATEMENT, node.getNodeType()); //$NON-NLS-1$
		ExpressionStatement statement = (ExpressionStatement) node;
		Expression expression = statement.getExpression();
		assertEquals("not a method invocation", ASTNode.FUNCTION_INVOCATION, expression.getNodeType()); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		assertEquals("not a simple name", ASTNode.SIMPLE_NAME, expression2.getNodeType()); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) expression2;
		ITypeBinding typeBinding4 = simpleName.resolveTypeBinding();
		assertTrue("An anonymous type binding", !typeBinding4.isAnonymous());		
		Name name = classInstanceCreation.getName();
		IBinding binding = name.resolveBinding();
		assertEquals("Wrong type", IBinding.TYPE, binding.getKind());
		ITypeBinding typeBinding5 = (ITypeBinding) binding;
		assertTrue("An anonymous type binding", !typeBinding5.isAnonymous());		
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=60078
	 * @deprecated using deprecated code
	 */
	public void test0547() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0547", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertEquals("Wrong number of problems", 1, unit.getProblems().length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("not a type declaration statement", ASTNode.TYPE_DECLARATION_STATEMENT, node.getNodeType()); //$NON-NLS-1$
		TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) node;
		TypeDeclaration typeDeclaration = typeDeclarationStatement.getTypeDeclaration();
		ITypeBinding typeBinding = typeDeclaration.resolveBinding();
		assertEquals("Wrong key", "Ltest0547/A$74$Local;", typeBinding.getKey());
		
		List bodyDeclarations = typeDeclaration.bodyDeclarations();
		assertEquals("wrong size", 3, bodyDeclarations.size());
		BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyDeclarations.get(0);
		assertEquals("not a type declaration statement", ASTNode.TYPE_DECLARATION, bodyDeclaration.getNodeType()); //$NON-NLS-1$
		TypeDeclaration typeDeclaration2 = (TypeDeclaration) bodyDeclaration;
		
		typeBinding = typeDeclaration2.resolveBinding();
		assertEquals("Wrong key", "Ltest0547/A$100$LocalMember;", typeBinding.getKey());
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=60581
	 */
	public void test0548() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0548", "PaletteStackEditPart.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, false);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48502
	 */
	public void test0549() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0549", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, false);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=48502
	 */
	public void test0550() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0550", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, false);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=60848
	 */
	public void test0551() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0551", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, false);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 1, problems.length); //$NON-NLS-1$
		IProblem problem = problems[0];
		assertEquals("wrong end position", source.length - 1, problem.getSourceEnd());
	}
	
	public void test0552() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0552", "Test.js");
		char[] source = sourceUnit.getSource().toCharArray();
		JavaScriptUnit result = (JavaScriptUnit) runConversion(sourceUnit, true);
		assertEquals("Got errors", 0, result.getProblems().length);
		TypeDeclaration declaration = (TypeDeclaration) result.types().get(0);
		Block body = declaration.getMethods()[0].getBody();
		ExpressionStatement expr = (ExpressionStatement) body.statements().get(0);
		FunctionInvocation invocation = (FunctionInvocation) expr.getExpression();
		InfixExpression node = (InfixExpression) invocation.arguments().get(0);
		ITypeBinding typeBinding = node.resolveTypeBinding();
		assertEquals("wrong type", "java.lang.String", typeBinding.getQualifiedName());
		checkSourceRange(node, "\"a\" + \"a\" + \"a\"", source);
		List extendedOperands = node.extendedOperands();
		assertEquals("Wrong size", 1, extendedOperands.size());
		Expression leftOperand = node.getLeftOperand();
		checkSourceRange(leftOperand, "\"a\"", source);
		typeBinding = leftOperand.resolveTypeBinding();
		assertEquals("wrong type", "java.lang.String", typeBinding.getQualifiedName());
		Expression rightOperand = node.getRightOperand();
		checkSourceRange(rightOperand, "\"a\"", source);
		typeBinding = rightOperand.resolveTypeBinding();
		assertEquals("wrong type", "java.lang.String", typeBinding.getQualifiedName());
		Expression expression = (Expression) extendedOperands.get(0);
		checkSourceRange(expression, "\"a\"", source);
		typeBinding = expression.resolveTypeBinding();
		assertEquals("wrong type", "java.lang.String", typeBinding.getQualifiedName());
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61946
	 */
	public void test0553() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0553", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertEquals("Not a field declaration", ASTNode.FIELD_DECLARATION, node.getNodeType());
		FieldDeclaration fieldDeclaration = (FieldDeclaration) node;
		List fragments = fieldDeclaration.fragments();
		assertEquals("Wrong size", 1, fragments.size());
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		IVariableBinding variableBinding = fragment.resolveBinding();
		assertNotNull("No binding", variableBinding);
		Object constantValue = variableBinding.getConstantValue();
		assertNull("Got a constant value", constantValue);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61946
	 */
	public void test0554() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0554", "B.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a return statement", ASTNode.RETURN_STATEMENT, node.getNodeType());
		ReturnStatement returnStatement = (ReturnStatement) node;
		Expression expression = returnStatement.getExpression();
		assertNotNull("No expression", expression);
		assertEquals("Not a method invocation", ASTNode.FUNCTION_INVOCATION, expression.getNodeType());
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		checkSourceRange(expression2, "A", source);
		ITypeBinding typeBinding = expression2.resolveTypeBinding();
		assertEquals("wrong type", "test0554.A", typeBinding.getQualifiedName());
		IVariableBinding[] fields = typeBinding.getDeclaredFields();
		assertEquals("Wrong size", 1, fields.length);
		IVariableBinding variableBinding = fields[0];
		Object constantValue = variableBinding.getConstantValue();
		assertNotNull("Missing constant", constantValue);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=61946
	 */
	public void test0555() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0555", "B.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Not a return statement", ASTNode.RETURN_STATEMENT, node.getNodeType());
		ReturnStatement returnStatement = (ReturnStatement) node;
		Expression expression = returnStatement.getExpression();
		assertNotNull("No expression", expression);
		assertEquals("Not a qualified name", ASTNode.QUALIFIED_NAME, expression.getNodeType());
		QualifiedName qualifiedName = (QualifiedName) expression;
		Name name = qualifiedName.getQualifier();
		checkSourceRange(name, "A", source);
		ITypeBinding typeBinding = name.resolveTypeBinding();
		assertEquals("wrong type", "test0555.A", typeBinding.getQualifiedName());
		IVariableBinding[] fields = typeBinding.getDeclaredFields();
		assertEquals("Wrong size", 1, fields.length);
		IVariableBinding variableBinding = fields[0];
		Object constantValue = variableBinding.getConstantValue();
		assertNotNull("No constant value", constantValue);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=62463
	 */
	public void test0556() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0556", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 1, 0);
		assertEquals("Not an expression statement", ASTNode.EXPRESSION_STATEMENT, node.getNodeType());
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertEquals("Not an method invocation", ASTNode.FUNCTION_INVOCATION, expression.getNodeType());
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		checkSourceRange(expression2, "(aa.bar())", source);
		SimpleName simpleName = methodInvocation.getName();
		checkSourceRange(simpleName, "size", source);
		checkSourceRange(expression, "(aa.bar()).size()", source);
		checkSourceRange(expressionStatement, "(aa.bar()).size();", source);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=62463
	 */
	public void test0557() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0557", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 1, 0);
		assertEquals("Not an expression statement", ASTNode.EXPRESSION_STATEMENT, node.getNodeType());
		ExpressionStatement expressionStatement = (ExpressionStatement) node;
		Expression expression = expressionStatement.getExpression();
		assertEquals("Not an method invocation", ASTNode.FUNCTION_INVOCATION, expression.getNodeType());
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		Expression expression2 = methodInvocation.getExpression();
		checkSourceRange(expression2, "(aa.bar())", source);
		SimpleName simpleName = methodInvocation.getName();
		checkSourceRange(simpleName, "get", source);
		checkSourceRange(expression, "(aa.bar()).get(0)", source);
		checkSourceRange(expressionStatement, "(aa.bar()).get(0);", source);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=65090
	 * @deprecated using deprecated code
	 */
	public void test0558() {
		String src = "\tSystem.out.println(\"Hello\");\n\tSystem.out.println(\"World\");\n";
		char[] source = src.toCharArray();
		ASTParser parser = ASTParser.newParser(AST.JLS2);
		parser.setKind (ASTParser.K_STATEMENTS);
		parser.setSource (source);
		ASTNode result = parser.createAST (null);
		assertNotNull("no result", result);
		assertEquals("Wrong type", ASTNode.BLOCK, result.getNodeType());
		Block block = (Block) result;
		List statements = block.statements();
		assertNotNull("No statements", statements);
		assertEquals("Wrong size", 2, statements.size());
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=65562
	 */
	public void test0559() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0559", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong type", ASTNode.IF_STATEMENT, node.getNodeType());
		IfStatement ifStatement = (IfStatement) node;
		Expression expression = ifStatement.getExpression();
		assertEquals("Wrong type", ASTNode.INFIX_EXPRESSION, expression.getNodeType());
		InfixExpression infixExpression = (InfixExpression) expression;
		Expression expression2 = infixExpression.getLeftOperand();
		assertEquals("Wrong type", ASTNode.FUNCTION_INVOCATION, expression2.getNodeType());
		FunctionInvocation methodInvocation = (FunctionInvocation) expression2;
		Expression expression3 = methodInvocation.getExpression();
		assertEquals("Wrong type", ASTNode.PARENTHESIZED_EXPRESSION, expression3.getNodeType());
		ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression3;
		Expression expression4 = parenthesizedExpression.getExpression();
		assertEquals("Wrong type", ASTNode.STRING_LITERAL, expression4.getNodeType());
		checkSourceRange(expression4, "\" \"", source);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=65562
	 */
	public void test0560() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0560", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong type", ASTNode.IF_STATEMENT, node.getNodeType());
		IfStatement ifStatement = (IfStatement) node;
		Expression expression = ifStatement.getExpression();
		assertEquals("Wrong type", ASTNode.INFIX_EXPRESSION, expression.getNodeType());
		InfixExpression infixExpression = (InfixExpression) expression;
		Expression expression2 = infixExpression.getLeftOperand();
		assertEquals("Wrong type", ASTNode.FUNCTION_INVOCATION, expression2.getNodeType());
		FunctionInvocation methodInvocation = (FunctionInvocation) expression2;
		Expression expression3 = methodInvocation.getExpression();
		assertEquals("Wrong type", ASTNode.PARENTHESIZED_EXPRESSION, expression3.getNodeType());
		ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression3;
		Expression expression4 = parenthesizedExpression.getExpression();
		assertEquals("Wrong type", ASTNode.STRING_LITERAL, expression4.getNodeType());
		checkSourceRange(expression4, "\" \"", source);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=65562
	 */
	public void test0561() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0561", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong type", ASTNode.IF_STATEMENT, node.getNodeType());
		IfStatement ifStatement = (IfStatement) node;
		Expression expression = ifStatement.getExpression();
		assertEquals("Wrong type", ASTNode.INFIX_EXPRESSION, expression.getNodeType());
		InfixExpression infixExpression = (InfixExpression) expression;
		Expression expression2 = infixExpression.getLeftOperand();
		assertEquals("Wrong type", ASTNode.FUNCTION_INVOCATION, expression2.getNodeType());
		FunctionInvocation methodInvocation = (FunctionInvocation) expression2;
		Expression expression3 = methodInvocation.getExpression();
		assertEquals("Wrong type", ASTNode.PARENTHESIZED_EXPRESSION, expression3.getNodeType());
		ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression3;
		Expression expression4 = parenthesizedExpression.getExpression();
		assertEquals("Wrong type", ASTNode.STRING_LITERAL, expression4.getNodeType());
		checkSourceRange(expression4, "\" \"", source);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=65562
	 */
	public void test0562() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0562", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong type", ASTNode.IF_STATEMENT, node.getNodeType());
		IfStatement ifStatement = (IfStatement) node;
		Expression expression = ifStatement.getExpression();
		assertEquals("Wrong type", ASTNode.INFIX_EXPRESSION, expression.getNodeType());
		InfixExpression infixExpression = (InfixExpression) expression;
		Expression expression2 = infixExpression.getLeftOperand();
		assertEquals("Wrong type", ASTNode.FUNCTION_INVOCATION, expression2.getNodeType());
		FunctionInvocation methodInvocation = (FunctionInvocation) expression2;
		Expression expression3 = methodInvocation.getExpression();
		assertEquals("Wrong type", ASTNode.PARENTHESIZED_EXPRESSION, expression3.getNodeType());
		ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression3;
		Expression expression4 = parenthesizedExpression.getExpression();
		assertEquals("Wrong type", ASTNode.STRING_LITERAL, expression4.getNodeType());
		checkSourceRange(expression4, "\" \"", source);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=65562
	 */
	public void test0563() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0563", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong type", ASTNode.IF_STATEMENT, node.getNodeType());
		IfStatement ifStatement = (IfStatement) node;
		Expression expression = ifStatement.getExpression();
		assertEquals("Wrong type", ASTNode.INFIX_EXPRESSION, expression.getNodeType());
		InfixExpression infixExpression = (InfixExpression) expression;
		Expression expression2 = infixExpression.getLeftOperand();
		assertEquals("Wrong type", ASTNode.FUNCTION_INVOCATION, expression2.getNodeType());
		FunctionInvocation methodInvocation = (FunctionInvocation) expression2;
		Expression expression3 = methodInvocation.getExpression();
		assertEquals("Wrong type", ASTNode.PARENTHESIZED_EXPRESSION, expression3.getNodeType());
		ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression3;
		Expression expression4 = parenthesizedExpression.getExpression();
		checkSourceRange(expression4, "new String()", source);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=65562
	 */
	public void test0564() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0564", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong type", ASTNode.IF_STATEMENT, node.getNodeType());
		IfStatement ifStatement = (IfStatement) node;
		Expression expression = ifStatement.getExpression();
		assertEquals("Wrong type", ASTNode.INFIX_EXPRESSION, expression.getNodeType());
		InfixExpression infixExpression = (InfixExpression) expression;
		Expression expression2 = infixExpression.getLeftOperand();
		assertEquals("Wrong type", ASTNode.FUNCTION_INVOCATION, expression2.getNodeType());
		FunctionInvocation methodInvocation = (FunctionInvocation) expression2;
		Expression expression3 = methodInvocation.getExpression();
		assertEquals("Wrong type", ASTNode.PARENTHESIZED_EXPRESSION, expression3.getNodeType());
		ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression3;
		Expression expression4 = parenthesizedExpression.getExpression();
		checkSourceRange(expression4, "new String()", source);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=65562
	 */
	public void test0565() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0565", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 0);
		assertEquals("Wrong type", ASTNode.IF_STATEMENT, node.getNodeType());
		IfStatement ifStatement = (IfStatement) node;
		Expression expression = ifStatement.getExpression();
		assertEquals("Wrong type", ASTNode.INFIX_EXPRESSION, expression.getNodeType());
		InfixExpression infixExpression = (InfixExpression) expression;
		Expression expression2 = infixExpression.getLeftOperand();
		assertEquals("Wrong type", ASTNode.FUNCTION_INVOCATION, expression2.getNodeType());
		FunctionInvocation methodInvocation = (FunctionInvocation) expression2;
		Expression expression3 = methodInvocation.getExpression();
		assertEquals("Wrong type", ASTNode.PARENTHESIZED_EXPRESSION, expression3.getNodeType());
		ParenthesizedExpression parenthesizedExpression = (ParenthesizedExpression) expression3;
		Expression expression4 = parenthesizedExpression.getExpression();
		checkSourceRange(expression4, "(/**/ String /**/) new String()", source);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=69349
	 */
	public void test0566() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0566", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertEquals("Wrong type", ASTNode.FUNCTION_DECLARATION, node.getNodeType());
		assertEquals("Wrong character", '}', source[node.getStartPosition() + node.getLength() - 1]);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=69349
	 */
	public void test0567() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0567", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		char[] source = sourceUnit.getSource().toCharArray();
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0);
		assertEquals("Wrong type", ASTNode.FUNCTION_DECLARATION, node.getNodeType());
		assertEquals("Wrong character", '}', source[node.getStartPosition() + node.getLength() - 1]);
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=74369
	 * @deprecated using deprecated code
	 */
	public void test0569() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0569", "A.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		final IProblem[] problems = unit.getProblems();
		assertEquals("Wrong number of problems", 0, problems.length); //$NON-NLS-1$
		ASTNode node = getASTNode(unit, 0, 0, 1);
		assertEquals("not a type declaration statement", ASTNode.TYPE_DECLARATION_STATEMENT, node.getNodeType()); //$NON-NLS-1$
		TypeDeclarationStatement typeDeclarationStatement = (TypeDeclarationStatement) node;
		TypeDeclaration typeDeclaration = typeDeclarationStatement.getTypeDeclaration();
		assertEquals("wrong name", "Local", typeDeclaration.getName().getIdentifier());
		assertNull("Got a javadoc", typeDeclaration.getJavadoc());
		node = getASTNode(unit, 0);
		assertEquals("not a type declaration", ASTNode.TYPE_DECLARATION, node.getNodeType()); //$NON-NLS-1$
		typeDeclaration = (TypeDeclaration) node;
		assertEquals("wrong name", "A", typeDeclaration.getName().getIdentifier());
		assertNotNull("No javadoc", typeDeclaration.getJavadoc());
		node = getASTNode(unit, 0, 0);
		assertEquals("not a method declaration", ASTNode.FUNCTION_DECLARATION, node.getNodeType()); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertEquals("wrong name", "method", methodDeclaration.getName().getIdentifier());
		assertNotNull("No javadoc", methodDeclaration.getJavadoc());
	}
	
	/*
	 * Ensures that the type binding from an import and the type binding from a type ref are equals
	 * when the AST is computed using IJavaScriptUnit#reconcile(...)
	 * (regression test for bug 83210 Unidentical ITypeBindings for same type from same AST from reconcile)
	 */
	public void test0570() throws CoreException {
		IJavaScriptUnit workingCopy = null;
		try {
			workingCopy = getWorkingCopy("/Converter/src/X.js", true);
			JavaScriptUnit unit = (JavaScriptUnit) buildAST(
				"import java.util.List;\n" +
				"public class X{\n" +
				"  List field;\n" +
				"}",
				workingCopy
			);
			ImportDeclaration importDeclaration = (ImportDeclaration) unit.imports().iterator().next();
			TypeDeclaration typeDeclaration = (TypeDeclaration) unit.types().iterator().next();
			FieldDeclaration fieldDeclaration = typeDeclaration.getFields()[0];
			Type type = fieldDeclaration.getType();
			IBinding importBinding = importDeclaration.resolveBinding();
			IBinding typeBinding = type.resolveBinding();
			assertEquals(importBinding,typeBinding);
		} finally {
			if (workingCopy != null)
				workingCopy.discardWorkingCopy();
		}
	}
	
	/*
	 * Ensures that the bindings for a member type in a .class file can be created.
	 */
	public void test0571() throws CoreException, IOException {
		try {
			IJavaScriptProject p = createJavaProject("P", new String[] {""}, new String[] {"CONVERTER_JCL_LIB"}, "");
			String source =
				"public class X {\n" +
				"  public class Y {\n" +
				"  }\n" +
				"}";
			addLibrary(p, "test0571.jar", "test0571.zip", new String[] {"X.js", source	}, "1.4");
			IClassFile classFile = getClassFile("P", "/P/test0571.jar", "", "X$Y.class");
			JavaScriptUnit unit = (JavaScriptUnit) runConversion(AST.JLS3, classFile, 0, true);
			IProblem[] problems = unit.getProblems();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0, length = problems.length; i < length; i++)
				Util.appendProblem(buffer, problems[i], source.toCharArray(), i);
			assertEquals("Unexpected problems", "", buffer.toString());
		} finally {
			deleteProject("P");
		}
	}
	
	/*
	 * Ensures that the method bindings of an anonymous type are correct.
	 */
	public void test0572() throws CoreException {
		IJavaScriptUnit workingCopy = null;
		try {
			workingCopy = getWorkingCopy("/Converter/src/X.js", true/*resolve*/);
			AnonymousClassDeclaration type = (AnonymousClassDeclaration) buildAST(
				"public class X {\n" +
				"  void foo() {\n" +
				"    new X() /*start*/{\n" +
				"      void bar() {}\n" +
				"    }/*end*/;\n" +
				"  }\n" +
				"}",
				workingCopy);
			ITypeBinding typeBinding = type.resolveBinding();
			assertBindingsEqual(
				"LX$40;.(LX;)V\n" + 
				"LX$40;.bar()V",
				typeBinding.getDeclaredMethods());
		} finally {
			if (workingCopy != null)
				workingCopy.discardWorkingCopy();
		}
	}

	/*
	 * Ensures that the Java element of a compilation is correct.
	 */
	public void test0573() throws CoreException {
		IJavaScriptUnit workingCopy = null;
		try {
			workingCopy = getWorkingCopy("/Converter/src/X.js", true/*resolve*/);
			JavaScriptUnit cu = (JavaScriptUnit) buildAST(
				"public class X {\n" +
				"}",
				workingCopy);
			assertElementEquals("Unexpected Java element", "[Working copy] X.java [in <default> [in src [in Converter]]]", cu.getJavaElement());
		} finally {
			if (workingCopy != null)
				workingCopy.discardWorkingCopy();
		}
	}
	/*
	 * Ensures that strings are not optimized when creating the AST through a reconcile.
	 * (regression test for bug 82830 AST: String concatenation represented as single node)
	 */
	public void test0574() throws CoreException {
		IJavaScriptUnit workingCopy = null;
		try {
			workingCopy = getWorkingCopy("/Converter/src/X.js", true/*resolve*/);
			ASTNode string = buildAST(
				"public class X {\n" +
				"  String s = /*start*/\"a\" + \"b\"/*end*/;\n" +
				"}",
				workingCopy);
			assertEquals("Unexpected node type", ASTNode.INFIX_EXPRESSION, string.getNodeType());
		} finally {
			if (workingCopy != null)
				workingCopy.discardWorkingCopy();
		}
	}
	
	/*
	 * Ensures that 2 different method bindings with the same return type are not "isEqualTo(...)".
	 * (regression test for bug 99978 MalformedTreeException on Inline Method)
	 */
	public void test0575() throws JavaScriptModelException {
		IJavaScriptUnit workingCopy = null;
		try {
    		workingCopy = getWorkingCopy("/Converter/src/X.js", true/*resolve*/);
	    	String contents =
				"public class X {\n" + 
				"	/*start1*/String foo(String o) {return null;}/*end1*/\n" + 
				"	/*start2*/String foo(Object o) {return null;}/*end2*/\n" + 
				"}";
		   	IBinding[] firstBatch = resolveBindings(contents, workingCopy);
		   	IBinding[] secondBatch = resolveBindings(contents, workingCopy);
		   	assertTrue("2 different method type bindings should not be equals", !firstBatch[0].isEqualTo(secondBatch[1]));
		} finally {
			if (workingCopy != null)
				workingCopy.discardWorkingCopy();
		}
	}
	
	/*
	 * Ensures that the binding key of a raw member type is correct.
	 * (regression test for bug 100549 Strange binding keys from AST on class file of nested type)
	 */
	public void test0576() throws CoreException, IOException {
		try {
			IJavaScriptProject project = createJavaProject("P1", new String[] {""}, new String[] {"CONVERTER_JCL15_LIB"}, "", "1.5");
			addLibrary(project, "lib.jar", "src.zip", new String[] {
				"/P1/p/X.js",
				"package p;\n" +
				"public class X<T> {\n" +
				"  /*start*/public class Member {\n" +
				"  }/*end*/\n" +
				"}",
			}, "1.5");
			IClassFile classFile = getClassFile("P1", "/P1/lib.jar", "p", "X$Member.class");
			String source = classFile.getSource();
			MarkerInfo markerInfo = new MarkerInfo(source);
			markerInfo.astStarts = new int[] {source.indexOf("/*start*/") + "/*start*/".length()};
			markerInfo.astEnds = new int[] {source.indexOf("/*end*/")};
			ASTNode node = buildAST(markerInfo, classFile);
			ITypeBinding binding = ((TypeDeclaration) node).resolveBinding();
			assertBindingKeyEquals("Lp/X<TT;>.Member;", binding.getKey());
		} finally {
			deleteProject("P1");
		}
	}
	
	/*
	 * Ensures that strings are not optimized when creating the AST through a reconcile
	 * even if the working copy was consistent.
	 * (regression test for bug 114909 AST: String concatenation represented as single node)
	 */
	public void test0577() throws CoreException {
		IJavaScriptUnit workingCopy = null;
		try {
			workingCopy = getWorkingCopy(
				"/Converter/src/X.js", 
				"public class X {\n" +
				"  String s = /*start*/\"a\" + \"b\"/*end*/;\n" +
				"}",
				true/*resolve*/);
			ASTNode string = buildAST(workingCopy);
			assertEquals("Unexpected node type", ASTNode.INFIX_EXPRESSION, string.getNodeType());
		} finally {
			if (workingCopy != null)
				workingCopy.discardWorkingCopy();
		}
	}
	


	/*
	 * Ensures that the start position of an argument that has a previous sibbling with a comment is correct
	 * (regression test for bug 80904 Quick Fix "Assign parameter to new field" doesn't appear with commented type)
	 */
	public void test0579() throws CoreException {
		IJavaScriptUnit workingCopy = null;
		try {
			workingCopy = getWorkingCopy(
				"/Converter/src/X.js", 
				"public class X {\n" +
				"  /*start*/void foo(Object/*first arg*/ arg1, Object arg2) {\n" +
				"  }/*end*/\n" +
				"}",
				true/*resolve*/);
			FunctionDeclaration method = (FunctionDeclaration) buildAST(workingCopy);
			SingleVariableDeclaration arg2 = (SingleVariableDeclaration) method.parameters().get(1);
			int start = arg2.getStartPosition();
			assertEquals("Unexpected range for arg2", "Object arg2", workingCopy.getSource().substring(start, start+arg2.getLength()));
		} finally {
			if (workingCopy != null)
				workingCopy.discardWorkingCopy();
		}
	}
	
	public void test0606() throws JavaScriptModelException {
		IJavaScriptUnit sourceUnit = getCompilationUnit("Converter", "src", "test0606", "X.js"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		ASTNode result = runConversion(sourceUnit, true);
		assertEquals("not a compilation unit", ASTNode.JAVASCRIPT_UNIT, result.getNodeType()); //$NON-NLS-1$
		JavaScriptUnit unit = (JavaScriptUnit) result;
		assertProblemsSize(unit, 0);
		unit.accept(new ASTVisitor() {
			public boolean visit(FunctionDeclaration methodDeclaration) {
				IFunctionBinding methodBinding = methodDeclaration.resolveBinding();
				IJavaScriptElement javaElement = methodBinding.getJavaElement();
				assertNotNull("No java element", javaElement);
				return false;
			}
		});
	}
	
	/**
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=173853
	 */
	public void test0607() throws CoreException {
		IJavaScriptUnit workingCopy = null;
		try {
			workingCopy = getWorkingCopy(
				"/Converter/src/X.js", 
				"public class X {\n" +
				"  void foo() {\n" +
				"    #\n" +
				"    /*start*/new Object() {\n" +
				"    }/*end*/;\n" +
				"  }\n" +
				"}",
				true/*resolve*/);
			ASTNode string = buildAST(null, workingCopy, true, true);
			assertEquals("Unexpected node type", ASTNode.CLASS_INSTANCE_CREATION, string.getNodeType());
			ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation) string;
			ITypeBinding resolveTypeBinding = classInstanceCreation.resolveTypeBinding();
			assertNotNull("Binding is null", resolveTypeBinding);
			IFunctionBinding[] declaredMethods = resolveTypeBinding.getDeclaredMethods();
			assertNotNull("Should have one method", declaredMethods);
			assertEquals("Should have one method", 1, declaredMethods.length);
			assertTrue("The method should be a default constructor", declaredMethods[0].isDefaultConstructor());
		} finally {
			if (workingCopy != null)
				workingCopy.discardWorkingCopy();
		}
	}
}
