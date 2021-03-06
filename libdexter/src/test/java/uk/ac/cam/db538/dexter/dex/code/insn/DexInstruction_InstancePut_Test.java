package uk.ac.cam.db538.dexter.dex.code.insn;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction22c;
import org.junit.Test;

import uk.ac.cam.db538.dexter.dex.code.Utils;
import uk.ac.cam.db538.dexter.hierarchy.HierarchyTest;

public class DexInstruction_InstancePut_Test extends HierarchyTest {

    @Test
    public void testParse_InstancePut() throws InstructionParseError {
        Utils.parseAndCompare(
            new Instruction[] {
                new Instruction22c(Opcode.IPUT_OBJECT, (byte) 0, (byte) 1, Utils.getFieldItem("Lcom/example/MyClass1;", "Ljava/lang/Object;", "TestField1")),
                new Instruction22c(Opcode.IPUT, (byte) 2, (byte) 3, Utils.getFieldItem("Lcom/example/MyClass2;", "I", "TestField2")),
                new Instruction22c(Opcode.IPUT, (byte) 4, (byte) 5, Utils.getFieldItem("Lcom/example/MyClass2;", "F", "TestField2B")),
                new Instruction22c(Opcode.IPUT_BOOLEAN, (byte) 6, (byte) 7, Utils.getFieldItem("Lcom/example/MyClass3;", "Z", "TestField3")),
                new Instruction22c(Opcode.IPUT_BYTE, (byte) 8, (byte) 9, Utils.getFieldItem("Lcom/example/MyClass4;", "B", "TestField4")),
                new Instruction22c(Opcode.IPUT_CHAR, (byte) 10, (byte) 11, Utils.getFieldItem("Lcom/example/MyClass5;", "C", "TestField5")),
                new Instruction22c(Opcode.IPUT_SHORT, (byte) 12, (byte) 13, Utils.getFieldItem("Lcom/example/MyClass6;", "S", "TestField6")),
                new Instruction22c(Opcode.IPUT_WIDE, (byte) 0, (byte) 1, Utils.getFieldItem("Lcom/example/MyClass5;", "J", "TestField5")),
                new Instruction22c(Opcode.IPUT_WIDE, (byte) 2, (byte) 3, Utils.getFieldItem("Lcom/example/MyClass6;", "D", "TestField6"))
            }, new String[] {
                "iput-object v0, {v1}com.example.MyClass1.TestField1",
                "iput-int-float v2, {v3}com.example.MyClass2.TestField2",
                "iput-int-float v4, {v5}com.example.MyClass2.TestField2B",
                "iput-boolean v6, {v7}com.example.MyClass3.TestField3",
                "iput-byte v8, {v9}com.example.MyClass4.TestField4",
                "iput-char v10, {v11}com.example.MyClass5.TestField5",
                "iput-short v12, {v13}com.example.MyClass6.TestField6",
                "iput-wide v0, {v1}com.example.MyClass5.TestField5",
                "iput-wide v2, {v3}com.example.MyClass6.TestField6",
            },
            this.hierarchy);
    }

    @Test(expected=Error.class)
    public void testParse_InstancePut_WrongType_Object() throws InstructionParseError {
        Utils.parseAndCompare(
            new Instruction22c(Opcode.IPUT_OBJECT, (byte) 0, (byte) 1, Utils.getFieldItem("Lcom/example/MyClass1;", "I", "TestField1")),
            "",
            this.hierarchy);
    }

    @Test(expected=Error.class)
    public void testParse_InstancePut_WrongType_Integer() throws InstructionParseError {
        Utils.parseAndCompare(
            new Instruction22c(Opcode.IPUT, (byte) 0, (byte) 1, Utils.getFieldItem("Lcom/example/MyClass1;", "Z", "TestField1")),
            "",
            this.hierarchy);
    }

    @Test(expected=Error.class)
    public void testParse_InstancePut_WrongType_Boolean() throws InstructionParseError {
        Utils.parseAndCompare(
            new Instruction22c(Opcode.IPUT_BOOLEAN, (byte) 0, (byte) 1, Utils.getFieldItem("Lcom/example/MyClass1;", "B", "TestField1")),
            "",
            this.hierarchy);
    }

    @Test(expected=Error.class)
    public void testParse_InstancePut_WrongType_Byte() throws InstructionParseError {
        Utils.parseAndCompare(
            new Instruction22c(Opcode.IPUT_BYTE, (byte) 0, (byte) 1, Utils.getFieldItem("Lcom/example/MyClass1;", "C", "TestField1")),
            "",
            this.hierarchy);
    }

    @Test(expected=Error.class)
    public void testParse_InstancePut_WrongType_Char() throws InstructionParseError {
        Utils.parseAndCompare(
            new Instruction22c(Opcode.IPUT_CHAR, (byte) 0, (byte) 1, Utils.getFieldItem("Lcom/example/MyClass1;", "S", "TestField1")),
            "",
            this.hierarchy);
    }

    @Test(expected=Error.class)
    public void testParse_InstancePut_WrongType_Short() throws InstructionParseError {
        Utils.parseAndCompare(
            new Instruction22c(Opcode.IPUT_SHORT, (byte) 0, (byte) 1, Utils.getFieldItem("Lcom/example/MyClass1;", "I", "TestField1")),
            "",
            this.hierarchy);
    }

    @Test(expected=Error.class)
    public void testParse_InstancePutWide_WrongType() throws InstructionParseError {
        Utils.parseAndCompare(
            new Instruction22c(Opcode.IPUT_WIDE, (byte) 0, (byte) 1, Utils.getFieldItem("Lcom/example/MyClass1;", "I", "TestField1")),
            "",
            this.hierarchy);
    }
}
