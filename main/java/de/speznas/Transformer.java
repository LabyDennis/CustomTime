package de.speznas;

import net.labymod.core.asm.LabyModCoreMod;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import net.minecraft.launchwrapper.IClassTransformer;
import net.labymod.support.util.Debug.EnumDebugMode;
import net.labymod.support.util.Debug;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

public class Transformer implements IClassTransformer
{

    static boolean isObfuscated = LabyModCoreMod.isObfuscated();
    private static String[] toTransform = {"bdb", "hu"};
    private static String[] toTransformForge = {"net.minecraft.client.multiplayer.WorldClient",
            "net.minecraft.network.play.server.S03PacketTimeUpdate"};

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        int index = isObfuscated ? Arrays.asList(toTransform).indexOf(transformedName) : Arrays.asList(toTransformForge).indexOf(transformedName);
        return index != -1 ?  transform(index, basicClass) : basicClass;
    }

    private static byte[] transform(int index, byte[] basicClass){

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

            switch (index){
                case 0: transformWorldClient(classNode);
                    break;
                case 1: if(isObfuscated)transformS03PacketTimeUpdate(classNode);
                    break;
            }

            classNode.accept(classWriter);
            return classWriter.toByteArray();

        }catch (Exception e){
            e.printStackTrace();
        }

        return basicClass;
    }

    private static void transformS03PacketTimeUpdate(ClassNode classNode) {
        final String processPacket = isObfuscated ? "a" : "processPacket";
        final String processPacketDesc = isObfuscated ? "(Lep;)V" : "(Lnet/minecraft/network/INetHandler;)V";

        for(MethodNode methodNode : classNode.methods){
            if(methodNode.name.equals(processPacket) && methodNode.desc.equals(processPacketDesc)){
                AbstractInsnNode targetNode = null;
                for(AbstractInsnNode instruction : methodNode.instructions.toArray()){
                    if(instruction.getOpcode() == ALOAD && instruction.getNext().getOpcode() == ALOAD){
                        targetNode = instruction;
                    }
                }
                if(targetNode != null){
                    InsnList toInsert = new InsnList();
                    toInsert.add(new VarInsnNode(ALOAD, 0));
                    toInsert.add(new FieldInsnNode(GETFIELD, isObfuscated ? "hu" : "S03PacketTimeUpdate", isObfuscated ? "b" : "getWorldTime", "J"));
                    toInsert.add(new FieldInsnNode(PUTSTATIC, "de/speznas/CustomTime", "servertime", "J"));
                    methodNode.instructions.insertBefore(targetNode, toInsert);
                }
            }
        }
    }




    private static void transformWorldClient(ClassNode classNode){
        final String setWorldTime = isObfuscated ? "b" : "setWorldTime";
        final String setWorldTimeDesc = isObfuscated ? "(J)V" : "(J)V";

        final String tick = isObfuscated ? "c" : "tick";
        final String tickDesc = isObfuscated ? "()V" : "()V";

        for(MethodNode methodNode : classNode.methods){
            if(methodNode.name.equals(setWorldTime) && methodNode.desc.equals(setWorldTimeDesc)){
                AbstractInsnNode targetNode = null;
                for(AbstractInsnNode instrunction : methodNode.instructions.toArray()){
                    if(instrunction.getOpcode() == ALOAD){
                        if(((VarInsnNode)instrunction).var == 0 && instrunction.getNext().getOpcode() == LLOAD){
                            targetNode = instrunction;
                            break;
                        }
                    }
                }
                if(targetNode != null){

                    InsnList toInsert = new InsnList();
                    methodNode.instructions.remove(targetNode.getNext());
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "de/speznas/CustomTime", "getSpeedEnabled", "()Z", false));
                    LabelNode newLabelNode = new LabelNode();
                    toInsert.add(new JumpInsnNode(IFEQ, newLabelNode));
                    toInsert.add(new FieldInsnNode(GETSTATIC, "de/speznas/CustomTime", "time", "J"));
                    LabelNode secondLabelNode = new LabelNode();
                    toInsert.add(new JumpInsnNode(GOTO, secondLabelNode));
                    toInsert.add(newLabelNode);
                    toInsert.add(new FrameNode(F_SAME1, 0, null, 1, new Object[]{isObfuscated ? "adm" : "World"}));
                    toInsert.add(new VarInsnNode(LLOAD, 1));
                    toInsert.add(secondLabelNode);
                    toInsert.add(new FrameNode(F_FULL, 2, new Object[]{isObfuscated ? "bdb" : "WorldClient", LONG},
                            2, new Object[]{isObfuscated ? "bdb" : "WorldClient", LONG}));

                    methodNode.instructions.insert(targetNode, toInsert);


                }else{
                    Debug.log(EnumDebugMode.ASM, "setWorldTime (b) wurde nicht gefunden!");
                }
            }

            if(methodNode.name.equals(tick) && methodNode.desc.equals(tickDesc)){
                AbstractInsnNode targetNode = null;
                for(AbstractInsnNode instrunction : methodNode.instructions.toArray()){
                    if(instrunction instanceof LdcInsnNode &&
                            instrunction.getPrevious().getPrevious().getOpcode() == ALOAD){

                        targetNode = instrunction.getPrevious().getPrevious();
                    }
                }
                if(targetNode != null){
                    InsnList toInsert = new InsnList();
                    toInsert.add(new MethodInsnNode(INVOKESTATIC, "de/speznas/CustomTime", "updateTime", "()V", false));
                    toInsert.add(new LabelNode());
                    methodNode.instructions.insertBefore(targetNode, toInsert);

                }else{
                    Debug.log(EnumDebugMode.ASM, "tick (c) wurde nicht gefunden!");
                }
            }
        }
    }
}
