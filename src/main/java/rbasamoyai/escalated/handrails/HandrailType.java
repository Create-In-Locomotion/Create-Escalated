package rbasamoyai.escalated.handrails;

import com.jozufozu.flywheel.api.struct.Batched;
import com.jozufozu.flywheel.api.struct.Instanced;
import com.jozufozu.flywheel.api.struct.StructWriter;
import com.jozufozu.flywheel.backend.gl.buffer.VecBuffer;
import com.jozufozu.flywheel.core.layout.BufferLayout;
import com.jozufozu.flywheel.core.model.ModelTransformer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import rbasamoyai.escalated.index.EscalatedInstanceFormats;
import rbasamoyai.escalated.index.EscalatedProgramSpecs;

public class HandrailType implements Instanced<HandrailData>, Batched<HandrailData> {

    @Override public HandrailData create() { return new HandrailData(); }

    @Override public BufferLayout getLayout() { return EscalatedInstanceFormats.HANDRAIL; }

    @Override public StructWriter<HandrailData> getWriter(VecBuffer backing) { return new HandrailWriterUnsafe(backing, this); }

    @Override public ResourceLocation getProgramSpec() { return EscalatedProgramSpecs.HANDRAIL; }

    @Override
    public void transform(HandrailData d, ModelTransformer.Params b) {
        b.shiftUV((builder, u, v) -> {
            float targetU = u - d.sourceU + d.minU;
            float targetV = v - d.sourceV + d.minV + d.scrollOffset;
            builder.uv(targetU, targetV);
        });
        // Copied from OrientedType#transform
        b.light(d.getPackedLight())
                .color(d.r, d.g, d.b, d.a)
                .translate(d.posX + d.pivotX, d.posY + d.pivotY, d.posZ + d.pivotZ)
                .multiply(new Quaternionf(d.qX, d.qY, d.qZ, d.qW))
                .translate(-d.pivotX, -d.pivotY, -d.pivotZ);
    }

}
