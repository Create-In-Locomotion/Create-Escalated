package rbasamoyai.escalated.index;

import com.jozufozu.flywheel.api.struct.StructType;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.handrails.HandrailData;
import rbasamoyai.escalated.handrails.HandrailType;

public class EscalatedMaterialSpecs {

    public static final StructType<HandrailData> HANDRAIL = new HandrailType();

    public static void init() {}

    public static class Locations {
        public static final ResourceLocation HANDRAIL = CreateEscalated.resource("handrail");
    }

}
