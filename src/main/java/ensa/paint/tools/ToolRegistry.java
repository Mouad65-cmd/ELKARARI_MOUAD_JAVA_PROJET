package ensa.paint.tools;

import java.util.EnumMap;
import java.util.Map;
import ensa.paint.model.ToolType;

/**
 * Registre associant chaque ToolType à son instance de Tool correspondante.
 */
public class ToolRegistry {

    private final Map<ToolType, Tool> tools = new EnumMap<>(ToolType.class);

    public ToolRegistry() {
        tools.put(ToolType.PENCIL, new PencilTool());
        tools.put(ToolType.BRUSH, new BrushTool());
        tools.put(ToolType.ERASER, new EraserTool());
        tools.put(ToolType.BUCKET, new BucketFillTool());
        tools.put(ToolType.PICKER, new PickerTool());
        tools.put(ToolType.TEXT, new TextTool());
        tools.put(ToolType.SELECTION, new SelectionTool());

        tools.put(ToolType.LINE, new LineTool());
        tools.put(ToolType.RECTANGLE, new RectangleTool());
        tools.put(ToolType.ROUNDED_RECTANGLE, new RoundedRectangleTool());
        tools.put(ToolType.ELLIPSE, new EllipseTool());
        tools.put(ToolType.TRIANGLE, new TriangleTool());
        tools.put(ToolType.STAR, new StarTool());
        tools.put(ToolType.ARROW, new ArrowTool());
    }

    public Tool getTool(ToolType type) {
        Tool tool = tools.get(type);
        return tool != null ? tool : tools.get(ToolType.PENCIL);
    }
}
