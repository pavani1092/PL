import java.util.ArrayList;
import java.util.Arrays;

public class Tokenizer {
	private ArrayList<String> tokenList;
	public boolean skippedSpace; // to check if there is a space previously
	public Tokenizer(String rawString) {
		rawString.trim();
		//replace extra spaces and tabs with single space
		rawString = rawString.replaceAll("[\\t\\n\\r]+"," ");
		//splitting the string on "(", ")"," ","." 
		String[] tokens = rawString.split("(\\s)|(?<=\\s)|(?<=\\()|(?=\\()|(?<=\\))|(?=\\))|(?<=\\.)|(?=\\.)");	
		tokenList = new ArrayList<String>(Arrays.asList(tokens));
	}
	
	public boolean hasMoreTokens() {
		return tokenList.size() > 0;
	}
	
	public String nextToken() {
		return tokenList.remove(0);
	}
	
	//Reads the next available token and skips a space
	public SExpr checkNextToken() {
		ParseInput.prev = ParseInput.current;
		ParseInput.current = null;
		skippedSpace = false;
		if(hasMoreTokens()) {
			String str = nextToken();
			if(str.isEmpty()||str.equals("")||str.equals(" ")) {
				SExpr temp = ParseInput.prev;
				ParseInput.current = checkNextToken();
				ParseInput.prev = temp;
				skippedSpace = true;
			}else if(str.equals("("))
				ParseInput.current = new SExpr("(", Utility.OPEN);
			else if(str.equals(")"))
				ParseInput.current = new SExpr(")", Utility.CLOSE);
			else if(str.equals("."))
				ParseInput.current = new SExpr(".", Utility.DOT);
			else if(str.matches("^[-+]?\\d+?$")) {
				ParseInput.current = new SExpr(str,Utility.INT_ATOM);
			}
			else if(str.matches("^[A-Za-z]+[0-9]*[A-Za-z]*?$")) {
				str = str.toUpperCase();
				if(SExpr.lookup.containsKey(str))
					ParseInput.current = SExpr.lookup.get(str);
				else
					ParseInput.current = new SExpr(str, Utility.SYM_ATOM);
			}else {
				ParseInput.ErrorMessage += "Invalid token "+str;
			}
		}else {
			ParseInput.ErrorMessage += "Empty input ";
		}
		return ParseInput.current;
	}
}
