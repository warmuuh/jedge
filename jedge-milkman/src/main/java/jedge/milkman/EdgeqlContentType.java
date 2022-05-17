package jedge.milkman;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import milkman.ui.plugin.ContentTypePlugin;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class EdgeqlContentType implements ContentTypePlugin {
	private static final String[] KEYWORDS = new String[] {
			"select", "false", "true"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String EDGEQL_KEYWORD = "(?<EDGEQLKEYWORD>" + KEYWORD_PATTERN + ")";
    private static final String EDGEQL_VALUE = "(?<=:)\\s*(?<EDGEQLVALUE>[\\w]+)";
    private static final String EDGEQL_VARIABLE = "(?<EDGEQLVARIABLE>\\$[\\w]+)";
    private static final String EDGEQL_DEFAULT = "(?<==)\\s*(?<EDGEQLDEFAULT>[^,\\)]+)";


	    
	private static final String EDGEQL_CURLY = "(?<EDGEQLCURLY>\\{|\\})";
	private static final String EDGEQL_ARRAY = "(?<EDGEQLARRAY>\\[|\\])";
	private static final Pattern FINAL_REGEX = Pattern.compile(
							EDGEQL_KEYWORD
			+ "|" + EDGEQL_VALUE
			+ "|" + EDGEQL_VARIABLE
			+ "|" + EDGEQL_DEFAULT
			);


	
	@Override
	public String getName() {
		return "Edgeql";
	}

	@Override
	public String getContentType() {
		return "application/edgeql";
	}

	@Override
	public boolean supportFormatting() {
		return false;
	}

	@Override
	public String formatContent(String text) {
		return null;
	}

	@Override
	public StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = FINAL_REGEX.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass
                    = matcher.group("EDGEQLKEYWORD") != null ? "keyword"
                    : matcher.group("EDGEQLVALUE") != null ? "value"
                    : matcher.group("EDGEQLVARIABLE") != null ? "keyword"
                    : matcher.group("EDGEQLDEFAULT") != null ? "value"
					: matcher.group("EDGEQLBOOL") != null ? "bool"
					: matcher.group("EDGEQLNUMBER") != null ? "number"
                    : "plain";
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
	}

}
