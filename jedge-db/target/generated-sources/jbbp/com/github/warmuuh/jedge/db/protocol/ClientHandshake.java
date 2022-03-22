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
public abstract class ClientHandshake implements com.github.warmuuh.jedge.db.protocol.ProtocolMessage  {
	private static final JBBPNamedFieldInfo _SField1FieldInfo = new JBBPNamedFieldInfo("name","connectionparam.name",5);
	private static final JBBPFieldTypeParameterContainer _SField1TypeParameter = new JBBPFieldTypeParameterContainer(JBBPByteOrder.BIG_ENDIAN,"string",null);
	private static final JBBPNamedFieldInfo _SField2FieldInfo = new JBBPNamedFieldInfo("value","connectionparam.value",8);
	private static final JBBPFieldTypeParameterContainer _SField2TypeParameter = new JBBPFieldTypeParameterContainer(JBBPByteOrder.BIG_ENDIAN,"string",null);
	
	/**
	 * The Constant contains parser flags
	 * @see JBBPParser#FLAG_SKIP_REMAINING_FIELDS_IF_EOF
	 * @see JBBPParser#FLAG_NEGATIVE_EXPRESSION_RESULT_AS_ZERO
	 */
	protected static final int _ParserFlags_ = 0;
	public static class CONNECTIONPARAM {

		public JBBPAbstractField name;
		public JBBPAbstractField value;
		
		private final ClientHandshake _Root_;

		public CONNECTIONPARAM (ClientHandshake root) {
			_Root_ = root;
		}

		public CONNECTIONPARAM read(final JBBPBitInputStream In) throws IOException {
			name = this._Root_.readCustomFieldType(this, In, _SField1TypeParameter, _SField1FieldInfo, 0, false, -1);
			value = this._Root_.readCustomFieldType(this, In, _SField2TypeParameter, _SField2FieldInfo, 0, false, -1);
			
			return this;
		}

		public CONNECTIONPARAM write(final JBBPBitOutputStream Out) throws IOException {
			this._Root_.writeCustomFieldType(this, Out, this.name, _SField1TypeParameter, _SField1FieldInfo, 0, false, -1);
			this._Root_.writeCustomFieldType(this, Out, this.value, _SField2TypeParameter, _SField2FieldInfo, 0, false, -1);
			
			return this;
		}
	}
	public static class PROTOCOLEXTENSION {
		public static class HEADER {

			public char code;
			public String value;
			
			private final ClientHandshake _Root_;

			public HEADER (ClientHandshake root) {
				_Root_ = root;
			}

			public HEADER read(final JBBPBitInputStream In) throws IOException {
				this.code = (char)In.readUnsignedShort(JBBPByteOrder.BIG_ENDIAN);
				this.value = In.readString(JBBPByteOrder.BIG_ENDIAN);
				
				return this;
			}

			public HEADER write(final JBBPBitOutputStream Out) throws IOException {
				Out.writeShort(this.code,JBBPByteOrder.BIG_ENDIAN);
				Out.writeString(this.value,JBBPByteOrder.BIG_ENDIAN);
				
				return this;
			}
		}

		public String name;
		public char num_headers;
		public HEADER [] header;
		
		private final ClientHandshake _Root_;

		public PROTOCOLEXTENSION (ClientHandshake root) {
			_Root_ = root;
		}

		public PROTOCOLEXTENSION read(final JBBPBitInputStream In) throws IOException {
			this.name = In.readString(JBBPByteOrder.BIG_ENDIAN);
			this.num_headers = (char)In.readUnsignedShort(JBBPByteOrder.BIG_ENDIAN);
			if (this.header == null || this.header.length != assrtExprNotNeg((int)this.num_headers)){ this.header = new HEADER[assrtExprNotNeg((int)this.num_headers)]; for(int I=0;I<assrtExprNotNeg((int)this.num_headers);I++){ this.header[I] = new HEADER(_Root_);}}for (int I=0;I<assrtExprNotNeg((int)this.num_headers);I++){ this.header[I].read(In); }
			
			return this;
		}

		public PROTOCOLEXTENSION write(final JBBPBitOutputStream Out) throws IOException {
			Out.writeString(this.name,JBBPByteOrder.BIG_ENDIAN);
			Out.writeShort(this.num_headers,JBBPByteOrder.BIG_ENDIAN);
			for (int I=0;I<assrtExprNotNeg((int)this.num_headers);I++){ this.header[I].write(Out); }
			return this;
		}
	}

	public char major_ver;
	public char minor_ver;
	public char num_params;
	public CONNECTIONPARAM [] connectionparam;
	public char num_extensions;
	public PROTOCOLEXTENSION [] protocolextension;
	

	public ClientHandshake () {
	}

	public ClientHandshake read(final JBBPBitInputStream In) throws IOException {
		this.major_ver = (char)In.readUnsignedShort(JBBPByteOrder.BIG_ENDIAN);
		this.minor_ver = (char)In.readUnsignedShort(JBBPByteOrder.BIG_ENDIAN);
		this.num_params = (char)In.readUnsignedShort(JBBPByteOrder.BIG_ENDIAN);
		if (this.connectionparam == null || this.connectionparam.length != assrtExprNotNeg((int)this.num_params)){ this.connectionparam = new CONNECTIONPARAM[assrtExprNotNeg((int)this.num_params)]; for(int I=0;I<assrtExprNotNeg((int)this.num_params);I++){ this.connectionparam[I] = new CONNECTIONPARAM(this);}}for (int I=0;I<assrtExprNotNeg((int)this.num_params);I++){ this.connectionparam[I].read(In); }
		this.num_extensions = (char)In.readUnsignedShort(JBBPByteOrder.BIG_ENDIAN);
		if (this.protocolextension == null || this.protocolextension.length != assrtExprNotNeg((int)this.num_extensions)){ this.protocolextension = new PROTOCOLEXTENSION[assrtExprNotNeg((int)this.num_extensions)]; for(int I=0;I<assrtExprNotNeg((int)this.num_extensions);I++){ this.protocolextension[I] = new PROTOCOLEXTENSION(this);}}for (int I=0;I<assrtExprNotNeg((int)this.num_extensions);I++){ this.protocolextension[I].read(In); }
		
		return this;
	}

	public ClientHandshake write(final JBBPBitOutputStream Out) throws IOException {
		Out.writeShort(this.major_ver,JBBPByteOrder.BIG_ENDIAN);
		Out.writeShort(this.minor_ver,JBBPByteOrder.BIG_ENDIAN);
		Out.writeShort(this.num_params,JBBPByteOrder.BIG_ENDIAN);
		for (int I=0;I<assrtExprNotNeg((int)this.num_params);I++){ this.connectionparam[I].write(Out); }Out.writeShort(this.num_extensions,JBBPByteOrder.BIG_ENDIAN);
		for (int I=0;I<assrtExprNotNeg((int)this.num_extensions);I++){ this.protocolextension[I].write(Out); }
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
