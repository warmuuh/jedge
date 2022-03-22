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
public class Authentication implements com.github.warmuuh.jedge.db.protocol.ProtocolMessage  {
	
	/**
	 * The Constant contains parser flags
	 * @see JBBPParser#FLAG_SKIP_REMAINING_FIELDS_IF_EOF
	 * @see JBBPParser#FLAG_NEGATIVE_EXPRESSION_RESULT_AS_ZERO
	 */
	protected static final int _ParserFlags_ = 0;

	public int auth_status;
	public byte [] data;
	

	public Authentication () {
	}

	public Authentication read(final JBBPBitInputStream In) throws IOException {
		this.auth_status = In.readInt(JBBPByteOrder.BIG_ENDIAN);
		this.data = In.readByteArray(-1, JBBPByteOrder.BIG_ENDIAN);
		
		return this;
	}

	public Authentication write(final JBBPBitOutputStream Out) throws IOException {
		Out.writeInt(this.auth_status,JBBPByteOrder.BIG_ENDIAN);
		Out.writeBytes(this.data, this.data.length, JBBPByteOrder.BIG_ENDIAN);
		
		return this;
	}
}
