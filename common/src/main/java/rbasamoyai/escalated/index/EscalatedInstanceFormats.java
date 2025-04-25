package rbasamoyai.escalated.index;

import com.jozufozu.flywheel.core.layout.BufferLayout;
import com.jozufozu.flywheel.core.layout.CommonItems;

public class EscalatedInstanceFormats {

    public static final BufferLayout HANDRAIL = orientedInstance().addItems(CommonItems.UV, CommonItems.VEC4, CommonItems.FLOAT).build();

    public static void init() {}

    private static BufferLayout.Builder orientedInstance() {
        return BufferLayout.builder()
                .addItems(CommonItems.LIGHT, CommonItems.RGBA) // BasicData
                .addItems(CommonItems.VEC3, CommonItems.VEC3, CommonItems.QUATERNION); // OrientedData
    }

}
