package com.github.warmuuh.jedge.db.protocol;

import com.igormaznitsa.jbbp.model.*;
import com.igormaznitsa.jbbp.io.*;
import com.igormaznitsa.jbbp.compiler.*;
import com.igormaznitsa.jbbp.compiler.tokenizer.*;
import java.io.IOException;
import java.util.*;

/**
 * Generated from JBBP script by internal JBBP Class Source Generator
 */
public abstract class ErrorResponse implements com.github.warmuuh.jedge.db.protocol.ProtocolMessage  {
	private static final JBBPNamedFieldInfo _SField1FieldInfo = new JBBPNamedFieldInfo("message","message",2);
	private static final JBBPFieldTypeParameterContainer _SField1TypeParameter = new JBBPFieldTypeParameterContainer(JBBPByteOrder.BIG_ENDIAN,"string",null);
	private static final JBBPNamedFieldInfo _SField2FieldInfo = new JBBPNamedFieldInfo("value","header.value",9);
	private static final JBBPFieldTypeParameterContainer _SField2TypeParameter = new JBBPFieldTypeParameterContainer(JBBPByteOrder.BIG_ENDIAN,"string",null);
	
	/**
	 * The Constant contains parser flags
	 * @see JBBPParser#FLAG_SKIP_REMAINING_FIELDS_IF_EOF
	 * @see JBBPParser#FLAG_NEGATIVE_EXPRESSION_RESULT_AS_ZERO
	 */
	protected static final int _ParserFlags_ = 0;
	public static class HEADER {

		public char code;
		public JBBPAbstractField value;
		
		private final ErrorResponse _Root_;

		public HEADER (ErrorResponse root) {
			_Root_ = root;
		}

		public HEADER read(final JBBPBitInputStream In) throws IOException {
			this.code = (char)In.readUnsignedShort(JBBPByteOrder.BIG_ENDIAN);
			value = this._Root_.readCustomFieldType(this, In, _SField2TypeParameter, _SField2FieldInfo, 0, false, -1);
			
			return this;
		}

		public HEADER write(final JBBPBitOutputStream Out) throws IOException {
			Out.writeShort(this.code,JBBPByteOrder.BIG_ENDIAN);
			this._Root_.writeCustomFieldType(this, Out, this.value, _SField2TypeParameter, _SField2FieldInfo, 0, false, -1);
			
			return this;
		}
	}

	public char severity;
	public int error_code;
	public JBBPAbstractField message;
	public char num_attributes;
	public HEADER [] header;
	

	public ErrorResponse () {
	}

	public ErrorResponse read(final JBBPBitInputStream In) throws IOException {
		this.severity = (char)(In.readByte() & 0xFF);
		this.error_code = In.readInt(JBBPByteOrder.BIG_ENDIAN);
		message = this.readCustomFieldType(this, In, _SField1TypeParameter, _SField1FieldInfo, 0, false, -1);
		this.num_attributes = (char)In.readUnsignedShort(JBBPByteOrder.BIG_ENDIAN);
		if (this.header == null || this.header.length != assrtExprNotNeg((int)this.num_attributes)){ this.header = new HEADER[assrtExprNotNeg((int)this.num_attributes)]; for(int I=0;I<assrtExprNotNeg((int)this.num_attributes);I++){ this.header[I] = new HEADER(this);}}for (int I=0;I<assrtExprNotNeg((int)this.num_attributes);I++){ this.header[I].read(In); }
		
		return this;
	}

	public ErrorResponse write(final JBBPBitOutputStream Out) throws IOException {
		Out.write(this.severity);
		Out.writeInt(this.error_code,JBBPByteOrder.BIG_ENDIAN);
		this.writeCustomFieldType(this, Out, this.message, _SField1TypeParameter, _SField1FieldInfo, 0, false, -1);
		Out.writeShort(this.num_attributes,JBBPByteOrder.BIG_ENDIAN);
		for (int I=0;I<assrtExprNotNeg((int)this.num_attributes);I++){ this.header[I].write(Out); }
		return this;
	}

	/**
	 * Reading of custom fields
	 * @param sourceStruct source structure holding the field, must not be null
	 * @param inStream the input stream, must not be null
	 * @param typeParameterContainer info about field type, must not be null
	 * @param nullableNamedFieldInfo info abut field name, it can be null
	 * @param extraValue value from extra field part, default value 0
	 * @param readWholeStream flag to read the stream as array till the stream end if true
	 * @param arraySize if array then it is zero or great
	 * @exception IOException if data can't be read
	 * @return read value as abstract field, must not be null
	 */
	public abstract JBBPAbstractField readCustomFieldType(Object sourceStruct, JBBPBitInputStream inStream, JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue, boolean readWholeStream, int arraySize) throws IOException;
	
	/**
	 * Writing custom fields
	 * @param sourceStruct source structure holding the field, must not be null
	 * @param outStream the output stream, must not be null
	 * @param fieldValue value to be written
	 * @param typeParameterContainer info about field type, must not be null
	 * @param nullableNamedFieldInfo info abut field name, it can be null
	 * @param extraValue value from extra field part, default value is 0
	 * @param wholeArray true if to write whole array
	 * @param arraySize if array then it is zero or great
	 * @exception IOException if data can't be written
	 */
	public abstract void writeCustomFieldType(Object sourceStruct, JBBPBitOutputStream outStream, JBBPAbstractField fieldValue, JBBPFieldTypeParameterContainer typeParameterContainer, JBBPNamedFieldInfo nullableNamedFieldInfo, int extraValue, boolean wholeArray, int arraySize) throws IOException;
	
	private static int assrtExprNotNeg(final int value) { if (value<0) throw new IllegalArgumentException("Negative value in expression"); return value; }
	

}
