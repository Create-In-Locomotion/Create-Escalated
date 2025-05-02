package rbasamoyai.escalated.handrails;

import com.jozufozu.flywheel.api.struct.StructType;
import com.jozufozu.flywheel.backend.gl.buffer.VecBuffer;
import com.jozufozu.flywheel.core.materials.BasicWriterUnsafe;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import org.lwjgl.system.MemoryUtil;

public class HandrailWriterUnsafe extends BasicWriterUnsafe<HandrailData> {

    public HandrailWriterUnsafe(VecBuffer backingBuffer, StructType<HandrailData> vertexType) {
        super(backingBuffer, vertexType);
    }

    /**
     * Adapted from {@link com.jozufozu.flywheel.core.materials.oriented.OrientedWriterUnsafe#writeInternal(OrientedData)}
     * @param d
     */
    @Override
    protected void writeInternal(HandrailData d) {
        long ptr = writePointer;
        super.writeInternal(d);

        MemoryUtil.memPutFloat(ptr + 6, d.posX);
        MemoryUtil.memPutFloat(ptr + 10, d.posY);
        MemoryUtil.memPutFloat(ptr + 14, d.posZ);
        MemoryUtil.memPutFloat(ptr + 18, d.pivotX);
        MemoryUtil.memPutFloat(ptr + 22, d.pivotY);
        MemoryUtil.memPutFloat(ptr + 26, d.pivotZ);
        MemoryUtil.memPutFloat(ptr + 30, d.qX);
        MemoryUtil.memPutFloat(ptr + 34, d.qY);
        MemoryUtil.memPutFloat(ptr + 38, d.qZ);
        MemoryUtil.memPutFloat(ptr + 42, d.qW);
        MemoryUtil.memPutFloat(ptr + 46, d.sourceU);
        MemoryUtil.memPutFloat(ptr + 50, d.sourceV);
        MemoryUtil.memPutFloat(ptr + 54, d.minU);
        MemoryUtil.memPutFloat(ptr + 58, d.minV);
        MemoryUtil.memPutFloat(ptr + 62, d.maxU);
        MemoryUtil.memPutFloat(ptr + 66, d.maxV);
        MemoryUtil.memPutFloat(ptr + 70, d.scrollOffset);
    }

}
