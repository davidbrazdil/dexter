package uk.ac.cam.db538.dexter.dex;

import java.util.Set;
import org.jf.dexlib.StringIdItem;
import org.jf.dexlib.EncodedValue.AnnotationEncodedValue;
import org.jf.dexlib.EncodedValue.ArrayEncodedSubValue;
import org.jf.dexlib.EncodedValue.ArrayEncodedValue;
import org.jf.dexlib.EncodedValue.EncodedValue;
import org.jf.dexlib.EncodedValue.EnumEncodedValue;
import org.jf.dexlib.EncodedValue.FieldEncodedValue;
import org.jf.dexlib.EncodedValue.MethodEncodedValue;
import org.jf.dexlib.EncodedValue.StringEncodedValue;
import org.jf.dexlib.EncodedValue.TypeEncodedValue;
import org.jf.dexlib.Util.AccessFlags;
import uk.ac.cam.db538.dexter.dex.field.DexField;
import uk.ac.cam.db538.dexter.dex.type.DexClassType;
import uk.ac.cam.db538.dexter.dex.type.DexFieldId;
import uk.ac.cam.db538.dexter.dex.type.DexMethodId;
import uk.ac.cam.db538.dexter.dex.type.DexPrototype;
import uk.ac.cam.db538.dexter.dex.type.DexRegisterType;
import uk.ac.cam.db538.dexter.dex.type.DexType;
import uk.ac.cam.db538.dexter.hierarchy.FieldDefinition;
import uk.ac.cam.db538.dexter.hierarchy.MethodDefinition;
import uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy;

public class DexUtils {
	
	
	public static int assembleAccessFlags(Set<AccessFlags> accessFlags) {
		int result = 0;
		for (final org.jf.dexlib.Util.AccessFlags flag : accessFlags) result |= flag.getValue();
		return result;
	}
	
	public static DexField getInstanceField(Dex dex, DexClassType fieldClass, String fieldName, DexRegisterType fieldType) {
		for (final uk.ac.cam.db538.dexter.dex.DexClass clazz : dex.getClasses()) if (clazz.getClassDef().getType().equals(fieldClass)) {
			for (final uk.ac.cam.db538.dexter.dex.field.DexInstanceField field : clazz.getInstanceFields()) if (field.getFieldDef().getFieldId().getName().equals(fieldName) && field.getFieldDef().getFieldId().getType().equals(fieldType)) return field;
			return null;
		}
		return null;
	}
	
	public static DexField getStaticField(Dex dex, DexClassType fieldClass, String fieldName, DexRegisterType fieldType) {
		for (final uk.ac.cam.db538.dexter.dex.DexClass clazz : dex.getClasses()) if (clazz.getClassDef().getType().equals(fieldClass)) {
			for (final uk.ac.cam.db538.dexter.dex.field.DexStaticField field : clazz.getStaticFields()) if (field.getFieldDef().getFieldId().getName().equals(fieldName) && field.getFieldDef().getFieldId().getType().equals(fieldType)) return field;
			return null;
		}
		return null;
	}
	
	public static String parseString(StringIdItem stringItem) {
		if (stringItem == null) return null; else return stringItem.getStringValue();
	}
	
	private static FieldDefinition findStaticField(DexClassType clsType, DexRegisterType fieldType, String name, RuntimeHierarchy hierarchy) {
		final uk.ac.cam.db538.dexter.dex.type.DexFieldId fieldId = DexFieldId.parseFieldId(name, fieldType, hierarchy.getTypeCache());
		final uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition classDef = hierarchy.getBaseClassDefinition(clsType);
		return classDef.getStaticField(fieldId);
	}
	
	private static MethodDefinition findStaticMethod(DexClassType clsType, DexPrototype prototype, String name, RuntimeHierarchy hierarchy) {
		final uk.ac.cam.db538.dexter.dex.type.DexMethodId methodId = DexMethodId.parseMethodId(name, prototype, hierarchy.getTypeCache());
		final uk.ac.cam.db538.dexter.hierarchy.BaseClassDefinition classDef = hierarchy.getBaseClassDefinition(clsType);
		return classDef.getMethod(methodId);
	}
	
	public static EncodedValue cloneEncodedValue(EncodedValue value, DexAssemblingCache asmCache) {
		final uk.ac.cam.db538.dexter.hierarchy.RuntimeHierarchy hierarchy = asmCache.getHierarchy();
		final uk.ac.cam.db538.dexter.dex.type.DexTypeCache typeCache = hierarchy.getTypeCache();
		switch (value.getValueType()) {
		case VALUE_ARRAY: 
			final org.jf.dexlib.EncodedValue.ArrayEncodedSubValue arrayValue = (ArrayEncodedSubValue)value;
			final boolean isSubValue = !(value instanceof ArrayEncodedValue);
			int innerValuesCount = arrayValue.values.length;
			final org.jf.dexlib.EncodedValue.EncodedValue[] innerValues = new EncodedValue[innerValuesCount];
			for (int i = 0; i < innerValuesCount; ++i) innerValues[i] = cloneEncodedValue(arrayValue.values[i], asmCache);
			if (isSubValue) return new ArrayEncodedSubValue(innerValues); else return new ArrayEncodedValue(innerValues);
		
		case VALUE_BOOLEAN: 
		
		case VALUE_BYTE: 
		
		case VALUE_CHAR: 
		
		case VALUE_DOUBLE: 
		
		case VALUE_FLOAT: 
		
		case VALUE_INT: 
		
		case VALUE_LONG: 
		
		case VALUE_NULL: 
		
		case VALUE_SHORT: 
			return value;
		
		case VALUE_ENUM: 
			final org.jf.dexlib.EncodedValue.EnumEncodedValue enumValue = (EnumEncodedValue)value;
			return new EnumEncodedValue(asmCache.getField(findStaticField(DexClassType.parse(enumValue.value.getContainingClass().getTypeDescriptor(), typeCache), DexRegisterType.parse(enumValue.value.getFieldType().getTypeDescriptor(), typeCache), enumValue.value.getFieldName().getStringValue(), hierarchy)));
		
		case VALUE_FIELD: 
			final org.jf.dexlib.EncodedValue.FieldEncodedValue fieldValue = (FieldEncodedValue)value;
			return new FieldEncodedValue(asmCache.getField(findStaticField(DexClassType.parse(fieldValue.value.getContainingClass().getTypeDescriptor(), typeCache), DexRegisterType.parse(fieldValue.value.getFieldType().getTypeDescriptor(), typeCache), fieldValue.value.getFieldName().getStringValue(), hierarchy)));
		
		case VALUE_METHOD: 
			final org.jf.dexlib.EncodedValue.MethodEncodedValue methodValue = (MethodEncodedValue)value;
			return new MethodEncodedValue(asmCache.getMethod(findStaticMethod(DexClassType.parse(methodValue.value.getContainingClass().getTypeDescriptor(), typeCache), DexPrototype.parse(methodValue.value.getPrototype(), typeCache), methodValue.value.getMethodName().getStringValue(), hierarchy)));
		
		case VALUE_STRING: 
			final org.jf.dexlib.EncodedValue.StringEncodedValue stringValue = (StringEncodedValue)value;
			return new StringEncodedValue(asmCache.getStringConstant(stringValue.value.getStringValue()));
		
		case VALUE_TYPE: 
			final org.jf.dexlib.EncodedValue.TypeEncodedValue typeValue = (TypeEncodedValue)value;
			return new TypeEncodedValue(asmCache.getType(DexType.parse(typeValue.value.getTypeDescriptor(), typeCache)));
		
		case VALUE_ANNOTATION: 
			final org.jf.dexlib.EncodedValue.AnnotationEncodedValue annotationValue = (AnnotationEncodedValue)value;
			final org.jf.dexlib.StringIdItem[] newNames = new StringIdItem[annotationValue.names.length];
			for (int i = 0; i < annotationValue.names.length; ++i) newNames[i] = asmCache.getStringConstant(annotationValue.names[i].getStringValue());
			final org.jf.dexlib.EncodedValue.EncodedValue[] newEncodedValues = new EncodedValue[annotationValue.values.length];
			for (int i = 0; i < annotationValue.values.length; ++i) newEncodedValues[i] = cloneEncodedValue(annotationValue.values[i], asmCache);
			return new AnnotationEncodedValue(asmCache.getType(DexType.parse(annotationValue.annotationType.getTypeDescriptor(), typeCache)), newNames, newEncodedValues);
		
		default: 
			throw new RuntimeException("Unexpected EncodedValue type: " + value.getValueType().name());
		
		}
	}
}