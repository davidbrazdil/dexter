package uk.ac.cam.db538.dexter.dex.type;

import uk.ac.cam.db538.dexter.dex.DexParsingCache;
import uk.ac.cam.db538.dexter.utils.Cache;
import lombok.Getter;
import lombok.val;

public class DexArrayType extends DexReferenceType {

  @Getter private final DexRegisterType ElementType;

  public DexArrayType(DexRegisterType elementType) {
    super("[" + elementType.getDescriptor(),
          elementType.getPrettyName() + "[]",
          1);
    ElementType = elementType;
  }

  public static DexArrayType parse(String typeDescriptor, DexParsingCache cache) {
    return cache.getArrayType(typeDescriptor);
  }

  public static Cache<String, DexArrayType> createCache(final DexParsingCache cache) {
    return new Cache<String, DexArrayType>() {
      @Override
      protected DexArrayType createNewEntry(String typeDescriptor) {
        if (!typeDescriptor.startsWith("["))
          throw new UnknownTypeException(typeDescriptor);

        val elementType = DexRegisterType.parse(typeDescriptor.substring(1), cache);
        return new DexArrayType(elementType);
      }
    };
  }
}
