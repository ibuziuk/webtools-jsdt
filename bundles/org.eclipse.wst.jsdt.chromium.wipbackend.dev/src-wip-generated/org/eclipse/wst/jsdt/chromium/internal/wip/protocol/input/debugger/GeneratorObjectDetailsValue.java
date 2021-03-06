// Generated source.
// Generator: org.eclipse.wst.jsdt.chromium.internal.wip.tools.protocolgenerator.Generator
// Origin: http://src.chromium.org/blink/trunk/Source/devtools/protocol.json@<unknown>

package org.eclipse.wst.jsdt.chromium.internal.wip.protocol.input.debugger;

/**
 Information about the generator object.
 */
@org.eclipse.wst.jsdt.chromium.internal.protocolparser.JsonType
public interface GeneratorObjectDetailsValue {
  /**
   Generator function.
   */
  org.eclipse.wst.jsdt.chromium.internal.wip.protocol.input.runtime.RemoteObjectValue function();

  /**
   Name of the generator function.
   */
  String functionName();

  /**
   Current generator object status.
   */
  Status status();

  /**
   If suspended, location where generator function was suspended (e.g. location of the last 'yield'). Otherwise, location of the generator function.
   */
  @org.eclipse.wst.jsdt.chromium.internal.protocolparser.JsonOptionalField
  org.eclipse.wst.jsdt.chromium.internal.wip.protocol.input.debugger.LocationValue location();

  /**
   Current generator object status.
   */
  public enum Status {
    RUNNING,
    SUSPENDED,
    CLOSED,
  }
}
