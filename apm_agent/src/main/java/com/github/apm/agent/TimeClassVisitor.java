package com.github.apm.agent;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class TimeClassVisitor extends ClassVisitor {
  private String className;

  public TimeClassVisitor(ClassVisitor cv, String className) {
    super(Opcodes.ASM5, cv);
    this.className = className;
  }

  // 扫描到每个方法都会进入，参数详情下一篇博文详细分析
  @Override
  public MethodVisitor visitMethod(int access, final String name, String desc, String signature,
      String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    // 过来待修改类的构造函数
    if (!name.equals("<init>") && mv != null) {
      mv = new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {
        // 方法进入时获取开始时间
        @Override
        public void onMethodEnter() {
          // 相当于com.blueware.agent.TimeUtil.setStartTime();
          this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/apm/agent/TimeUtil",
              "setStartTime", "()V", false);
        }

        // 方法退出时获取结束时间并计算执行时间
        @Override
        public void onMethodExit(int opcode) {
          // 相当于com.blueware.agent.TimeUtil.setEndTime();
          this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/apm/agent/TimeUtil", "setEndTime",
              "()V", false);
          // 向栈中压入类名称
          this.visitLdcInsn(className);
          // 向栈中压入方法名
          this.visitLdcInsn(name);
          // 相当于com.blueware.agent.TimeUtil.getExclusiveTime("com/blueware/agent/TestTime","testTime");
          this.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/apm/agent/TimeUtil",
              "getExclusiveTime", "(Ljava/lang/String;Ljava/lang/String;)J", false);
        }
      };
    }
    return mv;
  }
}
