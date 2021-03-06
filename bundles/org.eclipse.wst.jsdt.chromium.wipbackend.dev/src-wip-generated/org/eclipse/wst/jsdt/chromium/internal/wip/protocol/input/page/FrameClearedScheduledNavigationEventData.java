// Generated source.
// Generator: org.eclipse.wst.jsdt.chromium.internal.wip.tools.protocolgenerator.Generator
// Origin: http://src.chromium.org/blink/trunk/Source/devtools/protocol.json@<unknown>

package org.eclipse.wst.jsdt.chromium.internal.wip.protocol.input.page;

/**
 Fired when frame no longer has a scheduled navigation.
 */
@org.eclipse.wst.jsdt.chromium.internal.protocolparser.JsonType
public interface FrameClearedScheduledNavigationEventData {
  /**
   Id of the frame that has cleared its scheduled navigation.
   */
  String/*See org.eclipse.wst.jsdt.chromium.internal.wip.protocol.common.page.FrameIdTypedef*/ frameId();

  public static final org.eclipse.wst.jsdt.chromium.internal.wip.protocol.input.WipEventType<org.eclipse.wst.jsdt.chromium.internal.wip.protocol.input.page.FrameClearedScheduledNavigationEventData> TYPE
      = new org.eclipse.wst.jsdt.chromium.internal.wip.protocol.input.WipEventType<org.eclipse.wst.jsdt.chromium.internal.wip.protocol.input.page.FrameClearedScheduledNavigationEventData>("Page.frameClearedScheduledNavigation", org.eclipse.wst.jsdt.chromium.internal.wip.protocol.input.page.FrameClearedScheduledNavigationEventData.class) {
    @Override public org.eclipse.wst.jsdt.chromium.internal.wip.protocol.input.page.FrameClearedScheduledNavigationEventData parse(org.eclipse.wst.jsdt.chromium.internal.wip.protocol.input.WipGeneratedParserRoot parser, org.json.simple.JSONObject obj) throws org.eclipse.wst.jsdt.chromium.internal.protocolparser.JsonProtocolParseException {
      return parser.parsePageFrameClearedScheduledNavigationEventData(obj);
    }
  };
}
