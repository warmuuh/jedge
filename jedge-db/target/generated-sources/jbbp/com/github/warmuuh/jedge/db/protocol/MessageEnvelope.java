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
public class MessageEnvelope implements com.github.warmuuh.jedge.db.protocol.ProtocolMessage  {
	
	/**
	 * The Constant contains parser flags
	 * @see JBBPParser#FLAG_SKIP_REMAINING_FIELDS_IF_EOF
	 * @see JBBPParser#FLAG_NEGATIVE_EXPRESSION_RESULT_AS_ZERO
	 */
	protected static final int _ParserFlags_ = 0;

	public char mtype;
	public int message_length;
	public byte [] message;
	

	public MessageEnvelope () {
	}

	public MessageEnvelope read(final JBBPBitInputStream In) throws IOException {
		this.mtype = (char)(In.readByte() & 0xFF);
		this.message_length = In.readInt(JBBPByteOrder.BIG_ENDIAN);
		this.message = In.readByteArray(assrtExprNotNeg(((int)this.message_length-4)), JBBPByteOrder.BIG_ENDIAN);
		
		return this;
	}

	public MessageEnvelope write(final JBBPBitOutputStream Out) throws IOException {
		Out.write(this.mtype);
		Out.writeInt(this.message_length,JBBPByteOrder.BIG_ENDIAN);
		Out.writeBytes(this.message, assrtExprNotNeg(((int)this.message_length-4)), JBBPByteOrder.BIG_ENDIAN);
		
		return this;
	}

	private static int assrtExprNotNeg(final int value) { if (value<0) throw new IllegalArgumentException("Negative value in expression"); return value; }
	

}
