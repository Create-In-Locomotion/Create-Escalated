package rbasamoyai.escalated.index.fabric;

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import rbasamoyai.escalated.handrails.HandrailBlockEntity;
import rbasamoyai.escalated.handrails.HandrailVisual;
import rbasamoyai.escalated.walkways.WalkwayBlockEntity;
import rbasamoyai.escalated.walkways.WalkwayVisual;

public class EscalatedBlockEntitiesImpl {

    public static <T> NonNullUnaryOperator<BlockEntityBuilder<WalkwayBlockEntity, T>> walkwayVisual() {
        return b -> ((CreateBlockEntityBuilder<WalkwayBlockEntity, T>) b).visual(() -> WalkwayVisual::new);
    }

    public static <T> NonNullUnaryOperator<BlockEntityBuilder<HandrailBlockEntity, T>> handrailVisual() {
        return b -> ((CreateBlockEntityBuilder<HandrailBlockEntity, T>) b).visual(() -> HandrailVisual::new);
    }

}
