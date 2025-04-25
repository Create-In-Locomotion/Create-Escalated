package rbasamoyai.escalated.handrails;

import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class HandrailData extends OrientedData {

    float sourceU;
    float sourceV;
    float minU;
    float minV;
    float maxU;
    float maxV;
    float scrollOffset;

    public HandrailData setScrollTexture(SpriteShiftEntry spriteShift) {
        // Adapted from BeltData#setScrollTexture
        TextureAtlasSprite source = spriteShift.getOriginal();
        TextureAtlasSprite target = spriteShift.getTarget();

        this.sourceU = source.getU0();
        this.sourceV = source.getV0();
        this.minU = target.getU0();
        this.minV = target.getV0();
        this.maxU = target.getU1();
        this.maxV = target.getV1();
        this.markDirty();

        return this;
    }

    public HandrailData setScrollOffset(float scrollOffset) {
        this.scrollOffset = scrollOffset;
        this.markDirty();
        return this;
    }

}
