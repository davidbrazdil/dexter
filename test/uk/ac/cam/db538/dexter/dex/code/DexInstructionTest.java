package uk.ac.cam.db538.dexter.dex.code;

import static org.junit.Assert.*;
import lombok.val;

import org.jf.dexlib.DexFile;
import org.jf.dexlib.StringIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction10x;
import org.jf.dexlib.Code.Format.Instruction11n;
import org.jf.dexlib.Code.Format.Instruction11x;
import org.jf.dexlib.Code.Format.Instruction12x;
import org.jf.dexlib.Code.Format.Instruction21c;
import org.jf.dexlib.Code.Format.Instruction21h;
import org.jf.dexlib.Code.Format.Instruction21s;
import org.jf.dexlib.Code.Format.Instruction22x;
import org.jf.dexlib.Code.Format.Instruction31c;
import org.jf.dexlib.Code.Format.Instruction31i;
import org.jf.dexlib.Code.Format.Instruction32x;
import org.jf.dexlib.Code.Format.Instruction51l;
import org.junit.Test;

public class DexInstructionTest {

  private static DexInstruction compare(Instruction insn, String output) {
    val insnList = DexInstruction.parse(new Instruction[] { insn });
    assertEquals(1, insnList.size());
    val insnInsn = insnList.get(0);
    assertEquals(output, insnInsn.getOriginalAssembly());
    return insnInsn;
  }

  @Test
  public void testGetRegister_ReuseRegisters() {
    val insn = (DexInstruction_Move)
               compare(
                 new Instruction12x(Opcode.MOVE, (byte) 3, (byte) 3),
                 "move v3, v3");
    assertTrue(insn.getRegTo() == insn.getRegFrom());
  }

  @Test
  public void testNop() {
    compare(new Instruction10x(Opcode.NOP),
            "nop");
  }

  @Test
  public void testMove() {
    compare(new Instruction12x(Opcode.MOVE, (byte) 1, (byte) 2),
            "move v1, v2");
  }

  @Test
  public void testMoveObject() {
    compare(new Instruction12x(Opcode.MOVE_OBJECT, (byte) 1, (byte) 2),
            "move-object v1, v2");
  }

  @Test
  public void testMoveFrom16() {
    compare(new Instruction22x(Opcode.MOVE_FROM16, (short) 255, 65535),
            "move v255, v65535");
  }

  @Test
  public void testMoveObjectFrom16() {
    compare(new Instruction22x(Opcode.MOVE_OBJECT_FROM16, (short) 255, 65535),
            "move-object v255, v65535");
  }

  @Test
  public void testMove16() {
    compare(new Instruction32x(Opcode.MOVE_16, 65534, 65535),
            "move v65534, v65535");
  }

  @Test
  public void testMoveObject16() {
    compare(new Instruction32x(Opcode.MOVE_OBJECT_16, 65534, 65535),
            "move-object v65534, v65535");
  }

  @Test
  public void testMoveWide() {
    val insn = (DexInstruction_MoveWide)
               compare(
                 new Instruction12x(Opcode.MOVE_WIDE, (byte) 8, (byte) 5),
                 "move-wide v8, v5");
    assertEquals(8, insn.getRegTo1().getOriginalId());
    assertEquals(9, insn.getRegTo2().getOriginalId());
    assertEquals(5, insn.getRegFrom1().getOriginalId());
    assertEquals(6, insn.getRegFrom2().getOriginalId());
  }

  @Test
  public void testMoveWideFrom16() {
    val insn = (DexInstruction_MoveWide)
               compare(
                 new Instruction22x(Opcode.MOVE_WIDE_FROM16, (short) 253, 62435),
                 "move-wide v253, v62435");
    assertEquals(253, insn.getRegTo1().getOriginalId());
    assertEquals(254, insn.getRegTo2().getOriginalId());
    assertEquals(62435, insn.getRegFrom1().getOriginalId());
    assertEquals(62436, insn.getRegFrom2().getOriginalId());
  }

  @Test
  public void testMoveWide16() {
    val insn = (DexInstruction_MoveWide)
               compare(
                 new Instruction32x(Opcode.MOVE_WIDE_16, 60123, 62435),
                 "move-wide v60123, v62435");
    assertEquals(60123, insn.getRegTo1().getOriginalId());
    assertEquals(60124, insn.getRegTo2().getOriginalId());
    assertEquals(62435, insn.getRegFrom1().getOriginalId());
    assertEquals(62436, insn.getRegFrom2().getOriginalId());
  }

  @Test
  public void testMoveResult() {
    compare(new Instruction11x(Opcode.MOVE_RESULT, (byte) 234),
            "move-result v234");
  }

  @Test
  public void testMoveResultObject() {
    compare(new Instruction11x(Opcode.MOVE_RESULT_OBJECT, (byte) 234),
            "move-result-object v234");
  }

  @Test
  public void testMoveResultWide() {
    val insn = (DexInstruction_MoveResultWide)
               compare(
                 new Instruction11x(Opcode.MOVE_RESULT_WIDE, (byte) 233),
                 "move-result-wide v233");
    assertEquals(233, insn.getRegTo1().getOriginalId());
    assertEquals(234, insn.getRegTo2().getOriginalId());
  }

  @Test
  public void testMoveException() {
    compare(new Instruction11x(Opcode.MOVE_EXCEPTION, (byte) 231),
            "move-exception v231");
  }

  @Test
  public void testReturnVoid() {
    compare(new Instruction10x(Opcode.RETURN_VOID),
            "return-void");
  }

  @Test
  public void testReturn() {
    compare(new Instruction11x(Opcode.RETURN, (byte) 231),
            "return v231");
  }

  @Test
  public void testReturnObject() {
    compare(new Instruction11x(Opcode.RETURN_OBJECT, (byte) 230),
            "return-object v230");
  }

  @Test
  public void testReturnWide() {
    val insn = (DexInstruction_ReturnWide)
               compare(
                 new Instruction11x(Opcode.RETURN_WIDE, (byte) 235),
                 "return-wide v235");
    assertEquals(235, insn.getRegFrom1().getOriginalId());
    assertEquals(236, insn.getRegFrom2().getOriginalId());
  }

  @Test
  public void testConst4() {
    compare(new Instruction11n(Opcode.CONST_4, (byte) 13, (byte) 7),
            "const v13, #7");
    compare(new Instruction11n(Opcode.CONST_4, (byte) 13, (byte) -8),
            "const v13, #-8");
  }

  @Test
  public void testConst16() {
    compare(new Instruction21s(Opcode.CONST_16, (byte) 236, (short) 32082),
            "const v236, #32082");
    compare(new Instruction21s(Opcode.CONST_16, (byte) 236, (short) -32082),
            "const v236, #-32082");
  }

  @Test
  public void testConst() {
    compare(new Instruction31i(Opcode.CONST, (byte) 237, 0x01ABCDEF),
            "const v237, #28036591");
    compare(new Instruction31i(Opcode.CONST, (byte) 237, 0xABCDEF01),
            "const v237, #-1412567295");
  }

  @Test
  public void testConstHigh16() {
    compare(new Instruction21h(Opcode.CONST_HIGH16, (byte) 238, (short)0x1234),
            "const v238, #305397760");
    compare(new Instruction21h(Opcode.CONST_HIGH16, (byte) 238, (short)0xABCD),
            "const v238, #-1412628480");
  }

  @Test
  public void testConstWide16() {
    compare(new Instruction21s(Opcode.CONST_WIDE_16, (byte) 236, (short) 32082),
            "const-wide v236, #32082");
    val insn = (DexInstruction_ConstWide)
               compare(new Instruction21s(Opcode.CONST_WIDE_16, (byte) 236, (short) -32082),
                       "const-wide v236, #-32082");
    assertEquals(236, insn.getRegTo1().getOriginalId());
    assertEquals(237, insn.getRegTo2().getOriginalId());
  }

  @Test
  public void testConstWide32() {
    compare(new Instruction31i(Opcode.CONST_WIDE_32, (byte) 236, 0x01ABCDEF),
            "const-wide v236, #28036591");
    val insn = (DexInstruction_ConstWide)
               compare(new Instruction31i(Opcode.CONST_WIDE_32, (byte) 236, 0xABCDEF01),
                       "const-wide v236, #-1412567295");
    assertEquals(236, insn.getRegTo1().getOriginalId());
    assertEquals(237, insn.getRegTo2().getOriginalId());
  }

  @Test
  public void testConstWide() {
    compare(new Instruction51l(Opcode.CONST_WIDE, (byte) 236, 0x0102030405060708L),
            "const-wide v236, #72623859790382856");
    val insn = (DexInstruction_ConstWide)
               compare(new Instruction51l(Opcode.CONST_WIDE, (byte) 236, 0xFFFFFFFFFFFFFFFEL),
                       "const-wide v236, #-2");
    assertEquals(236, insn.getRegTo1().getOriginalId());
    assertEquals(237, insn.getRegTo2().getOriginalId());
  }

  @Test
  public void testConstWideHigh16() {
    compare(new Instruction21h(Opcode.CONST_WIDE_HIGH16, (byte) 236, (short) 0x1234),
            "const-wide v236, #1311673391471656960");
    val insn = (DexInstruction_ConstWide)
               compare(new Instruction21h(Opcode.CONST_WIDE_HIGH16, (byte) 236, (short) 0xFEDC),
                       "const-wide v236, #-82190693199511552");
    assertEquals(236, insn.getRegTo1().getOriginalId());
    assertEquals(237, insn.getRegTo2().getOriginalId());
  }

  private static StringIdItem getStringItem(String str) {
    return StringIdItem.internStringIdItem(new DexFile(), str);
  }

  @Test
  public void testConstString() {
    compare(new Instruction21c(Opcode.CONST_STRING, (byte) 236, getStringItem("Hello, world!")),
            "const-string v236, \"Hello, world!\"");
    // escaping characters
    compare(new Instruction21c(Opcode.CONST_STRING, (byte) 236, getStringItem("Hello, \"world!")),
            "const-string v236, \"Hello, \\\"world!\"");
    // cutting off after 15 characters
    compare(new Instruction21c(Opcode.CONST_STRING, (byte) 236, getStringItem("123456789012345")),
            "const-string v236, \"123456789012345\"");
    compare(new Instruction21c(Opcode.CONST_STRING, (byte) 236, getStringItem("1234567890123456")),
            "const-string v236, \"123456789012345...\"");
    compare(new Instruction21c(Opcode.CONST_STRING, (byte) 236, getStringItem("12345678901234\"")),
            "const-string v236, \"12345678901234\\...\"");
  }

  @Test
  public void testConstStringJumbo() {
    compare(new Instruction31c(Opcode.CONST_STRING_JUMBO, (byte) 236, getStringItem("Hello, world!")),
            "const-string v236, \"Hello, world!\"");
  }
}