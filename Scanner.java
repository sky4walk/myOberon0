import java.util.ArrayList;

//written by André Betz 
//http://www.andrebetz.de

public class Scanner {
	public static class TokenSym {
		private String m_Value;
		private int m_Type;
		private int m_Pos;

		/* EnumTypes */
		public static int STRING_LITERAL 	= 1;	
		public static int CONSTANTINT	   	= 2;
		public static int TOKEN			 	= 3;
		public static int CONSTANTLIT	 	= 4;	
		public static int IDENTIFIER     	= 5;
		/* Ende */

		TokenSym(int Pos,int Type,String value){
			m_Type = Type;
			m_Value = value;
			m_Pos = Pos;
		}
		public int getPos() {
			return m_Pos;
		}
		public String getElement() {
			return m_Value;
		}
		public int getType() {
			return m_Type;
		}
	}
	private String[] m_Tokens = {
			"*","DIV","MOD","&","+","-","OR","=","#","<","<=",">",
			">=",".",",",":",")","]","OF","THEN","DO","(","[","~",
			":=",";","END","ELSE","ELSEIF","IF","WHILE","ARRAY","/*",
			"RECORD","CONST","TYPE","VAR","PROCEDURE","BEGIN","MODULE",
			"INTEGER","POINTER","STRING","CHAR"
	};
	
	private char[] m_digits = {'0','1','2','3','4','5','6','7','8','9'};
	private char[] m_letters = {	'a','b','c','d','e','f','g','h','i','j','k','l','m',
							'n','o','p','q','r','s','t','u','v','w','x','y','z',
							'A','B','C','D','E','F','G','H','I','J','K','L','M',
							'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
	};
	private char[] m_EndSym = {'\n','\t',' ','\r'};
	
	private String m_File = null;
	private int m_SymPos = 0;
	private ArrayList m_TokenList = null; 
	
	public Scanner(String File){
		m_File = File;
	}

	public void Reset(){
		m_SymPos = 0;
	}
	
	public int CreateTokenList(){
		m_TokenList = new ArrayList();
		char[] chArr = m_File.toCharArray();
		int Size = m_File.length();
		int Pos = 0;
		int state = 0;
		String ScanBuffer = "";
		int Erg = -1;
		
		while(Pos<Size)	{
			switch(state){
			case 0:
			{
				Pos = DelNoSigns(chArr,Pos,m_EndSym);
				state = 1;
				break;
			}
			case 1:
			{
				ScanBuffer="";
				char sign = chArr[Pos];
				ScanBuffer += sign;
				Pos++;
				if(DoesContain(sign,m_digits)>=0){
					state = 2;
				}else if(DoesContain(sign,m_letters)>=0){
					state = 3;
				}else if(IsTokenPrefix(ScanBuffer)){
					state = 4;
				}else if(sign=='\"'){
					state = 5;
				}else if(sign=='\''){
					state = 6;
				}else{
					state = 99;
				}
				break;
			}
			case 2:
			{
				while(Pos<Size&&DoesContain(chArr[Pos],m_digits)>=0){
					ScanBuffer += chArr[Pos];
					Pos++;
				}
				TokenSym ts = new TokenSym(Pos,TokenSym.CONSTANTINT,ScanBuffer);
				m_TokenList.add(ts);
				state = 0;
			
				break;
			}
			case 3:
			{
				while(Pos<Size&&(DoesContain(chArr[Pos],m_digits)>=0||DoesContain(chArr[Pos],m_letters)>=0)){
					ScanBuffer += chArr[Pos];
					Pos++;
				}
				TokenSym ts = null;
				if(DoesContain(ScanBuffer,m_Tokens)>=0){
					ts = new TokenSym(Pos,TokenSym.TOKEN,ScanBuffer);					
				}else{
					ts = new TokenSym(Pos,TokenSym.IDENTIFIER,ScanBuffer);
				}
				m_TokenList.add(ts);
				state = 0;
				break;
			}
			case 4:
			{
				String tmpBuf = ScanBuffer;
				do{
					ScanBuffer = tmpBuf;
					tmpBuf += chArr[Pos];
					Pos++;										
				}while(IsTokenPrefix(tmpBuf));
				Pos--;
				
				if(DoesContain(ScanBuffer,m_Tokens)>=0){
					if(ScanBuffer.equals("/*")){
						state = 7;
					}else{
						TokenSym ts = new TokenSym(Pos,TokenSym.TOKEN,ScanBuffer);					
						m_TokenList.add(ts);
						state = 0;
					}
				}else{
					state = 3;
				}
				break;
			}
			case 5:
			{
				ScanBuffer = "";
				while(Pos<Size&&chArr[Pos]!='\"'){
					ScanBuffer+=chArr[Pos];
					Pos++;
				}
				Pos++;
				TokenSym ts = new TokenSym(Pos,TokenSym.STRING_LITERAL,ScanBuffer);					
				m_TokenList.add(ts);
				state = 0;
				break;
			}
			case 6:
			{
				ScanBuffer = "";
				while(Pos<Size&&chArr[Pos]!='\''){
					ScanBuffer+=chArr[Pos];
					Pos++;
				}
				Pos++;
				TokenSym ts = new TokenSym(Pos,TokenSym.CONSTANTLIT,ScanBuffer);					
				m_TokenList.add(ts);
				state = 0;
				break;
			}
			case 7:
			{
				while(Pos<Size&&chArr[Pos]!='*'){
					Pos++;
				}
				Pos++;
				state = 8;
				break;
			}
			case 8:
			{
				if(chArr[Pos]=='/'){
					state = 0;
				}else{
					state = 7;
				}
				Pos++;
				break;
			}
			default:
			{
				Erg = Pos;
				Pos = Size;
				break;
			}
			}
		}
		
		return Erg;
	}
	
	private boolean IsTokenPrefix(String Buf)
	{
		for(int i=0;i<m_Tokens.length;i++)
		{
			String rt = m_Tokens[i];
			if(IsPrefix(Buf,rt))
			{
				return true;
			}
		}
		return false;
	}

	private boolean IsPrefix(String Buf,String Terminal)
	{
		if(Terminal.length()>=Buf.length())
		{
			for(int i=0;i<Buf.length();i++)
			{
				if((Buf.toCharArray()[i])!=(Terminal.toCharArray()[i]))
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}
	protected int DelNoSigns(char[] Input, int spos, char[] NoSigns) {
		int newpos = spos;
		if(NoSigns==null){
			return spos;
		}
		int slen = Input.length;
		if(newpos<slen) {
			char sign = Input[newpos];
			while((DoesContain(sign,NoSigns)>=0)&&(newpos<slen)) {
				newpos++;
				if(newpos<slen) {
					sign = Input[newpos];
				}
			}
		}
		return newpos;
	}
	
	protected int DoesContain(char Sign, char[] SignList){
		int count = 0;
		while(count<SignList.length){
			if(Sign == SignList[count]){
				return count;	
			}
			count++;
		}
		return -1;
	}
	
	protected int DoesContain(String Val, String[] ValList){
		int count = 0;
		while(count<ValList.length){
			if(Val.equals(ValList[count])){
				return count;	
			}
			count++;
		}
		return -1;
	}
	
	public TokenSym GetSym(){
		if(m_TokenList!=null && m_SymPos<m_TokenList.size()){
			return (TokenSym)m_TokenList.get(m_SymPos++);
		}
		return null;
	}
}
