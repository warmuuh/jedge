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
public abstract class AuthenticationRequiredSaslPayload implements com.github.warmuuh.jedge.db.protocol.ProtocolMessage  {
	private static final JBBPNamedFieldInfo _SField1FieldInfo = new JBBPNamedFieldInfo("methods","methods",1);
	private static final JBBPFieldTypeParameterContainer _SField1TypeParameter = new JBBPFieldTypeParameterContainer(JBBPByteOrder.BIG_ENDIAN,"string",null);
	
	/**
	 * The Constant contains parser flags
	 * @see JBBPParser#FLAG_SKIP_REMAINING_FIELDS_IF_EOF
	 * @see JBBPParser#FLAG_NEGATIVE_EXPRESSION_RESULT_AS_ZERO
	 */
	protected static final int _ParserFlags_ = 0;

	public int nummethods;
	public JBBPAbstractField methods;
	

	public AuthenticationRequiredSaslPayload () {
	}

	public AuthenticationRequiredSaslPayload read(final JBBPBitInputStream In) throws IOException {
		this.nummethods = In.readInt(JBBPByteOrder.BIG_ENDIAN);
		methods = this.readCustomFieldType(this, In, _SField1TypeParameter, _SField1FieldInfo, 0, false, assrtExprNotNeg((int)this.nummethods));
		
		return this;
	}

	public AuthenticationRequiredSaslPayload write(final JBBPBitOutputStream Out) throws IOException {
		Out.writeInt(this.nummethods,JBBPByteOrder.BIG_ENDIAN);
		this.writeCustomFieldType(this, Out, this.methods, _SField1TypeParameter, _SField1FieldInfo, 0, false, assrtExprNotNeg((int)this.nummethods));
		
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
