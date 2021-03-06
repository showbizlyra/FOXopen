package net.foxopen.fox.module.serialiser.components.html;

import com.google.common.base.Joiner;
import net.foxopen.fox.module.MenuOutActionProvider;
import net.foxopen.fox.module.datanode.EvaluatedNode;
import net.foxopen.fox.module.serialiser.SerialisationContext;
import net.foxopen.fox.module.serialiser.fragmentbuilder.MustacheFragmentBuilder;
import net.foxopen.fox.module.serialiser.html.HTMLSerialiser;
import net.foxopen.fox.thread.devtoolbar.DevToolbarContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuOutWidgetHelper {
  public static final String MENU_FLOW_DOWN = "down";
  public static final String MENU_FLOW_ACROSS = "across";

  /**
   * Construct a menu out for a given MenuOutActionProvider and serialser
   *
   * @param pSerialisationContext
   * @param pSerialiser
   * @param pMenuOutNode
   */
  public static void buildWidget(SerialisationContext pSerialisationContext, HTMLSerialiser pSerialiser, MenuOutActionProvider pMenuOutNode) {
    if (pMenuOutNode.getActionList().size() == 0) {
      return;
    }
    else {
      Map<String, Object> lTemplateVars = new HashMap<>();
      lTemplateVars.put("Class", Joiner.on(" ").join(pMenuOutNode.getClasses()));
      lTemplateVars.put("Style", Joiner.on(" ").join(pMenuOutNode.getStyles()));
      lTemplateVars.put("Open", true);
      if (!MENU_FLOW_DOWN.equals(pMenuOutNode.getFlow())) {
        lTemplateVars.put("Flow", "flow-across");
      }

      List<Map<String, String>> lRenderedMenuItems = new ArrayList<>();
      for (EvaluatedNode lMenuItemEvalNode : pMenuOutNode.getActionList()){
        HTMLSerialiser.HTMLTempSerialiser lTempSerialiser = pSerialiser.getTempSerialiser();

        // Output debug information if turned on
        if (pSerialisationContext.getDevToolbarContext().isFlagOn(DevToolbarContext.Flag.HTML_GEN_DEBUG)) {
          SingleWidgetBuildHelper.outputDebugInformation(pSerialiser, lMenuItemEvalNode);
        }

        lTempSerialiser.getWidgetBuilder(lMenuItemEvalNode.getWidgetBuilderType()).buildWidget(pSerialisationContext, lTempSerialiser, lMenuItemEvalNode);
        Map<String, String> lRenderedItem = new HashMap<>(1);
        lRenderedItem.put("ItemHTML", lTempSerialiser.getOutput());
        lRenderedMenuItems.add(lRenderedItem);
      }
      lTemplateVars.put("MenuItems", lRenderedMenuItems);

      MustacheFragmentBuilder.applyMapToTemplate("html/MenuOutComponent.mustache", lTemplateVars, pSerialiser.getWriter());
    }
  }
}
