package uk.ac.cam.db538.dexter.dex.method;

import java.util.Set;

import org.jf.dexlib.ClassDataItem.EncodedMethod;
import org.jf.dexlib.Util.AccessFlags;

import uk.ac.cam.db538.dexter.dex.DexClass;
import uk.ac.cam.db538.dexter.dex.DexInstrumentationCache;
import uk.ac.cam.db538.dexter.dex.code.DexCode;
import uk.ac.cam.db538.dexter.dex.code.insn.InstructionParsingException;
import uk.ac.cam.db538.dexter.dex.type.UnknownTypeException;

public class DexDirectMethod extends DexMethodWithCode {

  public DexDirectMethod(DexClass parent, EncodedMethod methodInfo)
  throws UnknownTypeException, InstructionParsingException {
    super(parent, methodInfo);
  }

  public DexDirectMethod(DexClass parent, String name,
                         Set<AccessFlags> accessFlags, DexPrototype prototype, DexCode code) {
    super(parent, name, accessFlags, prototype, code, true);
  }

  @Override
  public void instrument(DexInstrumentationCache cache) {
    super.instrument(cache);

    this.prototype.instrumentMethod(this, this.parameterRegisters, this.parameterMappings, this.code.getInstrumentationState());
  }
}
