/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.debug.internal.crossfire.jsdi;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptBreakpoint;
import org.eclipse.wst.jsdt.debug.core.breakpoints.IJavaScriptLineBreakpoint;
import org.eclipse.wst.jsdt.debug.core.jsdi.ScriptReference;
import org.eclipse.wst.jsdt.debug.core.model.JavaScriptDebugModel;
import org.eclipse.wst.jsdt.debug.internal.core.JavaScriptDebugPlugin;
import org.eclipse.wst.jsdt.debug.internal.core.breakpoints.JavaScriptLineBreakpoint;
import org.eclipse.wst.jsdt.debug.internal.crossfire.transport.Attributes;

/**
 * Utility class to handle tracking remote to local breakpoints
 * 
 * @since 3.4
 */
public final class BreakpointTracker {

	private static Map breakpointHandles = new HashMap();
	
	/**
	 * Returns the {@link RemoteBreakpoint} with the given handle
	 * @param handle the handle, cannot be <code>null</code>
	 * @return the {@link RemoteBreakpoint} with the given handle or <code>null</code>
	 */
	public static final RemoteBreakpoint getBreakpoint(Number handle) {
		Assert.isNotNull(handle, Messages.BreakpointTracker_0);
		return (RemoteBreakpoint) breakpointHandles.get(handle);
	}
	
	/**
	 * Removes the {@link RemoteBreakpoint} with the given handle
	 * @param handle the breakpoint handle, cannot be <code>null</code>
	 * @return the {@link RemoteBreakpoint} that was removed or <code>null</code>
	 */
	public static RemoteBreakpoint removeBreakpoint(Number handle) {
		Assert.isNotNull(handle, Messages.BreakpointTracker_1);
		return (RemoteBreakpoint) breakpointHandles.remove(handle);
	}
	
	/**
	 * Add the breakpoint described by the given JSON to the handles list
	 * @param vm the {@link CFVirtualMachine}, cannot be <code>null</code>
	 * @param json the JSON map object, cannot be <code>null</code>
	 * @return the newly added {@link RemoteBreakpoint} or <code>null</code> if one could not be created and added
	 */
	public static RemoteBreakpoint addBreakpoint(CFVirtualMachine vm, Map json) {
		Assert.isNotNull(vm, Messages.BreakpointTracker_2);
		Assert.isNotNull(json, Messages.BreakpointTracker_3);
		Number handle = (Number) json.get(Attributes.HANDLE);
		if(handle != null) {
			RemoteBreakpoint bp = (RemoteBreakpoint) breakpointHandles.get(handle);
			if(bp == null) {
				bp = new RemoteBreakpoint(vm, 
					handle,
					(Map) json.get(Attributes.LOCATION),
					(Map) json.get(Attributes.ATTRIBUTES),
					(String)json.get(Attributes.TYPE));
				breakpointHandles.put(handle, bp);
			}
			return bp;
		}
		return null;
	}
	
	/**
	 * Locates the breakpoint for the handle given in the map and updates its attributes
	 * 
	 * @param json the JSON map, cannot be <code>null</code>
	 * @return the {@link RemoteBreakpoint} that was updated or <code>null</code> if nothing was updated
	 */
	public static RemoteBreakpoint updateBreakpoint(Map json) {
		Assert.isNotNull(json, Messages.BreakpointTracker_4);
		Number handle = (Number) json.get(Attributes.HANDLE);
		if(handle != null) {
			RemoteBreakpoint bp = (RemoteBreakpoint) breakpointHandles.get(handle);
			if(bp != null) {
				bp.setEnabled(RemoteBreakpoint.getEnabled(json));
				bp.setCondition(RemoteBreakpoint.getCondition(json));
			}
			return bp;
		}
		return null;
	}
	
	/**
	 * Create a local version of the breakpoint if one does not exist
	 * 
	 * @param vm the {@link CFVirtualMachine}, cannot be <code>null</code>
	 * @param json the JSON map describing the breakpoint, cannot be <code>null</code>
	 * @return the new {@link IJavaScriptBreakpoint} or <code>null</code> if it could not be created
	 */
	public static IJavaScriptBreakpoint createLocalBreakpoint(CFVirtualMachine vm, Map json) {
		Assert.isNotNull(vm, Messages.BreakpointTracker_5);
		Assert.isNotNull(json, Messages.BreakpointTracker_6);
		RemoteBreakpoint rb = addBreakpoint(vm, json);
		if(rb != null && rb.isLineBreakpoint()) {
			IJavaScriptBreakpoint bp = findLocalBreakpoint(rb);
			if(bp != null) {
				return bp;
			}
			ScriptReference script = rb.vm.findScript(rb.getUrl());
			if(script != null) {
				IFile file = JavaScriptDebugPlugin.getResolutionManager().getFile(script);
				if(file != null) {
					HashMap attributes = new HashMap();
					attributes.put(IJavaScriptBreakpoint.TYPE_NAME, null);
					attributes.put(IJavaScriptBreakpoint.SCRIPT_PATH, file.getFullPath().makeAbsolute().toString());
					attributes.put(IJavaScriptBreakpoint.ELEMENT_HANDLE, null);
					String condition = rb.getCondition();
					if(condition != null) {
						attributes.put(JavaScriptLineBreakpoint.CONDITION, condition);
						attributes.put(JavaScriptLineBreakpoint.CONDITION_ENABLED, Boolean.TRUE);
						attributes.put(JavaScriptLineBreakpoint.CONDITION_SUSPEND_ON_TRUE, Boolean.TRUE);
					}
					try {
						return JavaScriptDebugModel.createLineBreakpoint(file, rb.getLine(), -1, -1, attributes, true);
					}
					catch(DebugException de) {}
				}
			}
		}
		return null;
	}
	
	/**
	 * Tries to locate and update the local version of the given {@link RemoteBreakpoint}
	 * @param rb the {@link RemoteBreakpoint}, cannot be <code>null</code>
	 * @return the {@link IJavaScriptBreakpoint} that was updated or <code>null</code> if no update occurred
	 */
	public static IJavaScriptBreakpoint updateLocalBreakpoint(RemoteBreakpoint rb) {
		Assert.isNotNull(rb, Messages.BreakpointTracker_8);
		//TODO
		return null;
	}
	
	/**
	 * Removes the local breakpoint given its remote equivalent
	 * @param handle the remote breakpoint, cannot be <code>null</code>
	 */
	public static void removeLocalBreakpoint(Number handle) {
		Assert.isNotNull(handle, Messages.BreakpointTracker_9);
		RemoteBreakpoint rb = removeBreakpoint(handle);
		if(rb != null) {
			IJavaScriptBreakpoint jsbp = findLocalBreakpoint(rb);
			if(jsbp != null) {
				try {
					jsbp.delete();
				} catch (CoreException e) {
				}
			}
		}
	}
	
	/**
	 * Finds the local equivalent {@link IJavaScriptBreakpoint} given the {@link RemoteBreakpoint}
	 * 
	 * @param breakpoint the {@link RemoteBreakpoint}, cannot be <code>null</code>
	 * @return the local equivalent of the given {@link RemoteBreakpoint} or <code>null</code> if one does not exist
	 */
	public static final IJavaScriptBreakpoint findLocalBreakpoint(RemoteBreakpoint breakpoint) {
		Assert.isNotNull(breakpoint, Messages.BreakpointTracker_7);
		ScriptReference script = breakpoint.vm.findScript(breakpoint.getUrl());
		if(script != null) {
			IFile file = JavaScriptDebugPlugin.getResolutionManager().getFile(script);
			if(file != null) {
				IBreakpoint[] bps = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(JavaScriptDebugModel.MODEL_ID);
				for (int i = 0; i < bps.length; i++) {
					if(bps[i] instanceof IJavaScriptLineBreakpoint) {
						IJavaScriptLineBreakpoint jsbp = (IJavaScriptLineBreakpoint) bps[i];
						try {
							String scriptpath = jsbp.getScriptPath();
							if(file.getFullPath().isPrefixOf(new Path(scriptpath)) &&
									breakpoint.getLine() == jsbp.getLineNumber()) {
								//potential match, now check the location, for now simply check the line number
								return jsbp;
							}
						}
						catch(CoreException ce) {}
					}
					else {
						IJavaScriptBreakpoint jsbp = (IJavaScriptBreakpoint) bps[i];
						try {
							String scriptpath = jsbp.getScriptPath();
							//script load / exception breakpoints have no further location than the script
							if(file.getFullPath().isPrefixOf(new Path(scriptpath))) {
								return jsbp;
							}
						}
						catch(CoreException ce) {}
					}
					
				}
			}
		}
		return null;
	}
}
