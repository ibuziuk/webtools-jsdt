// Generated source.
// Generator: org.eclipse.wst.jsdt.chromium.sdk.internal.wip.tools.protocolgenerator.Generator
// Origin: http://src.chromium.org/blink/trunk/Source/devtools/protocol.json@<unknown>

package org.eclipse.wst.jsdt.chromium.sdk.internal.wip.protocol.input.page;

/**
 Fired when frame has been attached to its parent.
 */
@org.eclipse.wst.jsdt.chromium.sdk.internal.protocolparser.JsonType
public interface FrameAttachedEventData {
  /**
   Id of the frame that has been attached.
   */
  String/*See org.eclipse.wst.jsdt.chromium.sdk.internal.wip.protocol.common.page.FrameIdTypedef*/ frameId();

  /**
   Parent frame identifier.
   */
  String/*See org.eclipse.wst.jsdt.chromium.sdk.internal.wip.protocol.common.page.FrameIdTypedef*/ parentFrameId();

  public static final org.eclipse.wst.jsdt.chromium.sdk.internal.wip.protocol.input.WipEventType<org.eclipse.wst.jsdt.chromium.sdk.internal.wip.protocol.input.page.FrameAttachedEventData> TYPE
      = new org.eclipse.wst.jsdt.chromium.sdk.internal.wip.protocol.input.WipEventType<org.eclipse.wst.jsdt.chromium.sdk.internal.wip.protocol.input.page.FrameAttachedEventData>("Page.frameAttached", org.eclipse.wst.jsdt.chromium.sdk.internal.wip.protocol.input.page.FrameAttachedEventData.class) {
    @Override public org.eclipse.wst.jsdt.chromium.sdk.internal.wip.protocol.input.page.FrameAttachedEventData parse(org.eclipse.wst.jsdt.chromium.sdk.internal.wip.protocol.input.WipGeneratedParserRoot parser, org.json.simple.JSONObject obj) throws org.eclipse.wst.jsdt.chromium.sdk.internal.protocolparser.JsonProtocolParseException {
      return parser.parsePageFrameAttachedEventData(obj);
    }
  };
}