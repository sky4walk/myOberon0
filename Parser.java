//written by André Betz 
//http://www.andrebetz.de

// module = "MODULE" ident ";" declarations ["BEGIN" StatementSequence] "END" ident "."
// declarations =   ["CONST" {ident "=" expression ";"}]
//                  ["TYPE"  {ident "=" type ";"}]
//                  ["VAR"   {IdentList ":" type ";"}]
//			{ProcedureDeclaration ";"}
// ProcedureDeclaration = ProcedureHeading ";" ProcedureBody
// ProcedureHeading = "PROCEDURE" ident [FormalParameters] [":" Type]
// ProcedureBody  = declarations ["BEGIN" StatementSequence] "END" ident
// FormalParameters = "(" [FPSection {";" FPSection}] ")"
// FPSection = ["VAR"] IdentList ":" type
// type = "INTEGER" | ArrayType | RecordType | "STRING" | "POINTER"
// RecordType = "RECORD" FieldList { ";" FieldList} "END"
// FieldList = [IdentList ":" type  ]
// IdentList = Identifier { "," Identifier }
// ArrayType = "ARRAY" expression "OF" type
// StatementSequence = statement { ";" statement}
// statement = [ProcedureAssign | IfStatement | WhileStatement]
// WhileStatement = "WHILE" expression "DO" StatementSequence "END"
// IfStatement = "IF" expression "THEN" StatementSequence
//               { "ELSIF" expression "THEN" StatementSequence}
//               [ "ELSE" StatementSequence] "END"
// ProcedureAssign = ident (assignment | ProcedureCall)
// ProcedureCall = "(" [ActualParameters] ")"
// assignment = selector ":=" expression
// ActualParameters = [expression {"," expression} ]
// expression = SimpleExpression [("=" |"#"|"<"|"<="|">"|">=") SimpleExpression]
// SimpleExpression = ["+"|"-"] term {("+"|"-"|"OR") term}
// term = factor {("*"|"DIV"|"MOD"|"&") factor}
// factor = ident selector | integer | "(" expression ")" | "~" factor
// selector = {"." ident | "[" expression "]"}


public class Parser {
	public static class ParseTree {
		private ParseTree m_Next = null;
		private ParseTree m_Depth = null;
		private Scanner.TokenSym m_ts = null;
		private String m_NonTerm = null;
		
		ParseTree(Scanner.TokenSym ts){
			m_ts = ts;
		}
		ParseTree(String Nonterm){
			m_NonTerm = Nonterm;
		}
		public ParseTree getM_Depth() {
			return m_Depth;
		}
		public ParseTree setM_Depth(ParseTree depth) {
			m_Depth = depth;
			return depth;
		}
		public ParseTree getM_Next() {
			return m_Next;
		}
		public ParseTree setM_Next(ParseTree next) {
			m_Next = next;
			return next;
		}
		public String getM_NonTerm() {
			return m_NonTerm;
		}
		public Scanner.TokenSym getM_ts() {
			return m_ts;
		}
		public void Print(){
				Print(this,0);
		}
		private static void Print(ParseTree pt,int DepthNr){
			while(pt!=null){
				String Token ="";
				for(int i=0;i<DepthNr;i++){
					Token += " ";
				}
				Scanner.TokenSym ts = pt.getM_ts();
				if(ts!=null){
					Token += ts.getType() + " : "+ ts.getElement();
				}else{
					Token += pt.getM_NonTerm();
				}
				System.out.println(Token);
				if(pt.getM_Depth()!=null){
					Print(pt.getM_Depth(),DepthNr+1);
				}
				pt = pt.getM_Next();
			}
		}
	}
	private Scanner m_scn = null;
	private ParseTree m_pt = null;
	private Scanner.TokenSym m_actSym = null;
	
	Parser(Scanner scn){
		m_scn = scn;
	}
	
	public boolean Parse(){
		m_actSym = m_scn.GetSym();
		m_pt = new ParseTree("module");
		return module(m_pt);
	}
	public Scanner.TokenSym GetActSymobl(){
		return m_actSym;
	}
	public ParseTree getParseTree(){
		return m_pt;
	}
	
	private void OutputError(String Msg){
		System.out.println(Msg + " : " + m_actSym.getPos());
	}
	protected boolean module(ParseTree pt){
		// module = "MODULE" ident ";" declarations ["BEGIN" StatementSequence] "END" ident "."
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("MODULE")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.IDENTIFIER){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(";")){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
					if(declarations(pt.setM_Depth(new ParseTree("declarations")))){
					}else{
						pt.setM_Depth(null);
						return false;
					}
					if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("BEGIN")){
						pt = pt.setM_Next(new ParseTree(m_actSym));
						m_actSym = m_scn.GetSym();
						if(StatementSequence(pt.setM_Depth(new ParseTree("StatementSequence")))){							
						}
					}
					if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("END")){
						pt = pt.setM_Next(new ParseTree(m_actSym));
						m_actSym = m_scn.GetSym();
						if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.IDENTIFIER){
							pt = pt.setM_Next(new ParseTree(m_actSym));
							m_actSym = m_scn.GetSym();
							if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(".")){
								pt = pt.setM_Next(new ParseTree(m_actSym));								
							}else{
								OutputError("Synthaxerror ZeichenNr");
								return false;																	
							}
						}else{
							OutputError("Synthaxerror ZeichenNr");
							return false;								
						}
					}else{
						OutputError("Synthaxerror ZeichenNr");
						return false;
					}
				}else{
					OutputError("Synthaxerror ZeichenNr");
					return false;
				}
			}else{
				OutputError("Synthaxerror ZeichenNr");
				return false;
			}
		}else{
			OutputError("Synthaxerror ZeichenNr");
			return false;
		}
		return true;
	}
	
	protected boolean declarations(ParseTree pt){
		// declarations =   ["CONST" {ident "=" expression ";"}]
		//                  ["TYPE"  {ident "=" type ";"}]
		//                  ["VAR"   {IdentList ":" type ";"}]
		//					{ProcedureDeclaration ";"}
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("CONST")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			while(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.IDENTIFIER){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("=")){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
					if(expression(pt.setM_Depth(new ParseTree("expression")))){
						if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(";")){
							pt = pt.setM_Next(new ParseTree(m_actSym));
							m_actSym = m_scn.GetSym();
						}else{
							OutputError("Synthaxerror ZeichenNr");
							return false;
						}
					}else{
						pt.setM_Depth(null);
						return false;
					}
				}else{
					OutputError("Synthaxerror ZeichenNr");
					return false;
				}
			}
		}
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("TYPE")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			while(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.IDENTIFIER){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("=")){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
					if(type(pt.setM_Depth(new ParseTree("type")))){
						if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(";")){
							pt = pt.setM_Next(new ParseTree(m_actSym));
							m_actSym = m_scn.GetSym();
						}else{
							OutputError("Synthaxerror ZeichenNr");
							return false;
						}
					}else{
						OutputError("Synthaxerror ZeichenNr");
						return false;
					}
				}else{
					OutputError("Synthaxerror ZeichenNr");
					return false;
				}
			}
		}
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("VAR")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			while(IdentList(pt.setM_Depth(new ParseTree("type")))){
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(":")){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
					if(type(pt.setM_Depth(new ParseTree("type")))){
						if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(";")){
							pt = pt.setM_Next(new ParseTree(m_actSym));
							m_actSym = m_scn.GetSym();
						}else{
							OutputError("Synthaxerror ZeichenNr");
							return false;
						}
					}else{
						pt.setM_Depth(null);
						return false;
					}
				}else{
					OutputError("Synthaxerror ZeichenNr");
					return false;
				}
			}
		}
		while(ProcedureDeclaration(pt.setM_Depth(new ParseTree("ProcedureDeclaration")))){
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(";")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
			}else{
				OutputError("Synthaxerror ZeichenNr");
				return false;
			}
		}
		return true;
	}
	
	protected boolean ProcedureDeclaration(ParseTree pt){
		// ProcedureDeclaration = ProcedureHeading ";" ProcedureBody
		if(ProcedureHeading(pt.setM_Depth(new ParseTree("ProcedureHeading"))))	{
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(";")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(ProcedureBody(pt.setM_Depth(new ParseTree("ProcedureBody"))))	{
				}else{
					OutputError("Synthaxerror ZeichenNr");
					return false;									
				}
			}else{
				OutputError("Synthaxerror ZeichenNr");
				return false;				
			}
		}else{
			pt.setM_Depth(null);
			return false;
		}
		return true;
	}

	protected boolean ProcedureHeading(ParseTree pt){
		//ProcedureHeading = "PROCEDURE" ident [FormalParameters] [":" Type]
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("PROCEDURE")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.IDENTIFIER){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(FormalParameters(pt.setM_Depth(new ParseTree("FormalParameters")))){
				}
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(":")){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
					if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("TYPE")){
						pt = pt.setM_Next(new ParseTree(m_actSym));
						m_actSym = m_scn.GetSym();
					}else{
						OutputError("Synthaxerror ZeichenNr");
						return false;						
					}
				}
			}else{
				OutputError("Synthaxerror ZeichenNr");
				return false;
			}
		}else{
			return false;
		}
		return true;
	}
	
	protected boolean ProcedureBody(ParseTree pt){
		// ProcedureBody  = declarations ["BEGIN" StatementSequence] "END" ident
		if(declarations(pt.setM_Depth(new ParseTree("declarations")))){
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("BEGIN")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(StatementSequence(pt.setM_Depth(new ParseTree("StatementSequence")))){							
				}
			}
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("END")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.IDENTIFIER){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
				}else{
					OutputError("Synthaxerror ZeichenNr");
					return false;					
				}
			}else{
				OutputError("Synthaxerror ZeichenNr");
				return false;
			}
		}else{
			pt.setM_Depth(null);
			return false;
		}
		return true;
	}

	protected boolean FormalParameters(ParseTree pt){
		// FormalParameters = "(" [FPSection {";" FPSection}] ")"
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("(")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			if(FPSection(pt.setM_Depth(new ParseTree("FPSection")))){							
				while(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(";")){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
					if(FPSection(pt.setM_Depth(new ParseTree("FPSection")))){							
					}
				}
			}
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(")")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
			}else{
				OutputError("Synthaxerror ZeichenNr");
				return false;
			}
		}else{
			OutputError("Synthaxerror ZeichenNr");
			return false;
		}
		return true;
	}
	
	protected boolean FPSection(ParseTree pt){
		// FPSection = ["VAR"] IdentList ":" type
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("VAR")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
		}
		if(IdentList(pt.setM_Depth(new ParseTree("IdentList")))){
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(":")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(type(pt.setM_Depth(new ParseTree("type")))){
				}else{
					OutputError("Synthaxerror ZeichenNr");
					return false;					
				}
			}else{
				OutputError("Synthaxerror ZeichenNr");
				return false;	
			}
		}else{
			pt.setM_Depth(null);
			return false;
		}		
		return true;
	}

	protected boolean type(ParseTree pt){
		// type = "INTEGER" | ArrayType | RecordType | "STRING" | "POINTER"
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("INTEGER")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
		}else if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("POINTER")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
		}else if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("STRING")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
		}else if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("CHAR")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
		}else if(ArrayType(pt.setM_Depth(new ParseTree("ArrayType")))){
		}else if(RecordType(pt.setM_Depth(new ParseTree("RecordType")))){
		}else{
			OutputError("Synthaxerror Type erwartet");
			return false;
		}
		return true;
	}
	
	protected boolean RecordType(ParseTree pt){
		// RecordType = "RECORD" FieldList { ";" FieldList} "END"
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("RECORD")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			if(FieldList(pt.setM_Depth(new ParseTree("FieldList")))){
				while(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(";")){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
					if(FieldList(pt.setM_Depth(new ParseTree("FieldList")))){
					}
				}				
			}
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("END")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
			}else{
				OutputError("Synthaxerror Type erwartet");
				return false;
			}
		}else{
			pt.setM_Depth(null);
			return false;
		}
		return true;
	}

	protected boolean FieldList(ParseTree pt){
		// FieldList = [IdentList ":" type  ]
		if(IdentList(pt.setM_Depth(new ParseTree("IdentList")))){
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(":")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(type(pt.setM_Depth(new ParseTree("type")))){
				}else{
					OutputError("Synthaxerror Type erwartet");
					return false;					
				}
			}else{
				OutputError("Synthaxerror \":\" erwartet");
				return false;				
			}
		}else{
			pt.setM_Depth(null);
			return false;
		}
		return true;
	}
	
	protected boolean IdentList(ParseTree pt){
		// IdentList = Identifier { "," Identifier }
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.IDENTIFIER){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			while(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(",")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.IDENTIFIER){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
				}else{
					return false;
				}
			}				
		}else{
			return false;
		}
		return true;
	}
	
	protected boolean ArrayType(ParseTree pt){
		// ArrayType = "ARRAY" expression "OF" type
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("ARRAY")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			if(expression(pt.setM_Depth(new ParseTree("expression")))){
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("OF")){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
					if(type(pt.setM_Depth(new ParseTree("type")))){
					}else{
						OutputError("Synthaxerror type erwartet");
						return false;											
					}
				}else{
					OutputError("Synthaxerror OF erwartet");
					return false;					
				}
			}else{
				OutputError("Synthaxerror expression erwartet");
				return false;					
			}
		}else{
			return false;
		}
		return true;
	}
	
	protected boolean StatementSequence(ParseTree pt){
		//StatementSequence = statement { ";" statement}
		if(statement(pt.setM_Depth(new ParseTree("statement")))){
			while(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(";")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(statement(pt.setM_Depth(new ParseTree("statement")))){
				}
			}
		}else{
			pt.setM_Depth(null);
			return false;
		}
		return true;
	}

	protected boolean statement(ParseTree pt){
		// statement = [ProcedureAssign | IfStatement | WhileStatement]
		if(ProcedureAssign(pt.setM_Depth(new ParseTree("ProcedureAssign")))){
		}else if(IfStatement(pt.setM_Depth(new ParseTree("IfStatement")))){
		}else if(WhileStatement(pt.setM_Depth(new ParseTree("WhileStatement")))){
		}else{
			pt.setM_Depth(null);
			return false;
		}
		return true;
	}

	protected boolean WhileStatement(ParseTree pt){
		// WhileStatement = "WHILE" expression "DO" StatementSequence "END"
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("WHILE")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			if(expression(pt.setM_Depth(new ParseTree("expression")))){
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("DO")){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
					if(StatementSequence(pt.setM_Depth(new ParseTree("StatementSequence")))){
					}
					if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("END")){
						pt = pt.setM_Next(new ParseTree(m_actSym));
						m_actSym = m_scn.GetSym();
					}else{
						OutputError("Synthaxerror END erwartet");
						return false;											
					}
				}else{
					OutputError("Synthaxerror DO erwartet");
					return false;					
				}
			}else{
				OutputError("Synthaxerror expression erwartet");
				return false;
			}
		}else{
			return false;
		}
		return true;
	}

	protected boolean IfStatement(ParseTree pt){
		// IfStatement = "IF" expression "THEN" StatementSequence
		//               { "ELSIF" expression "THEN" StatementSequence}
		//               [ "ELSE" StatementSequence] "END"
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("IF")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			if(expression(pt.setM_Depth(new ParseTree("expression")))){
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("THEN")){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
					if(StatementSequence(pt.setM_Depth(new ParseTree("StatementSequence")))){
						while(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("ELSIF")){
							pt = pt.setM_Next(new ParseTree(m_actSym));
							m_actSym = m_scn.GetSym();
							if(expression(pt.setM_Depth(new ParseTree("expression")))){
								if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("THEN")){
									pt = pt.setM_Next(new ParseTree(m_actSym));
									m_actSym = m_scn.GetSym();
									if(StatementSequence(pt.setM_Depth(new ParseTree("StatementSequence")))){
									}else{
										OutputError("Synthaxerror StatementSequence erwartet");
										return false;						
									}
								}else{
									OutputError("Synthaxerror THEN erwartet");
									return false;						
								}
							}else{
								OutputError("Synthaxerror expression erwartet");
								return false;														
							}			
						}
						if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("ELSE")){
							pt = pt.setM_Next(new ParseTree(m_actSym));
							m_actSym = m_scn.GetSym();
							if(StatementSequence(pt.setM_Depth(new ParseTree("StatementSequence")))){
							}else{
								OutputError("Synthaxerror StatementSequence erwartet");
								return false;						
							}
						}
						if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("END")){
							pt = pt.setM_Next(new ParseTree(m_actSym));
							m_actSym = m_scn.GetSym();
						}
					}else{
						OutputError("Synthaxerror END erwartet");
						return false;						
					}
				}else{
					OutputError("Synthaxerror THEN erwartet");
					return false;
				}
			}else{
				OutputError("Synthaxerror expression erwartet");
				return false;
			}
		}else{
			return false;
		}
		return true;
	}
	
	protected boolean ProcedureAssign(ParseTree pt){
		// ProcedureAssign = ident (assignment | ProcedureCal)
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.IDENTIFIER){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			if(ProcedureCall(pt.setM_Depth(new ParseTree("ProcedureCall")))){
			}else if(assignment(pt.setM_Depth(new ParseTree("assignment")))){
			}else{
				OutputError("Synthaxerror ProcedureCall or assignment erwartet");
				return false;
			}
		}else{
			return false;
		}
		return true;
	}

	protected boolean ProcedureCall(ParseTree pt){
		// ProcedureCall = "(" [ActualParameters] ")"
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("(")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			if(ActualParameters(pt.setM_Depth(new ParseTree("ActualParameters")))){
			}
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(")")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
			}else{
				OutputError("Synthaxerror \")\" erwartet");
				return false;											
			}
		}else{
			return false;
		}
		return true;
	}

	protected boolean assignment(ParseTree pt){
		// assignment = selector ":=" expression
		if(selector(pt.setM_Depth(new ParseTree("selector")))){
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(":=")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(expression(pt.setM_Depth(new ParseTree("expression")))){
				}else{
					OutputError("Synthaxerror expression erwartet");
					return false;																
				}
			}else{
				return false;
			}
		}else{
			pt.setM_Depth(null);
			return false;
		}
		return true;
	}

	protected boolean ActualParameters(ParseTree pt){
		//ActualParameters = [expression {"," expression} ]
		if(expression(pt.setM_Depth(new ParseTree("expression")))){
			while(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(",")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(expression(pt.setM_Depth(new ParseTree("expression")))){
				}else{
					OutputError("Synthaxerror expression erwartet");
					return false;																					
				}
			}			
		}else{
			pt.setM_Depth(null);
			return false;
		}
		return true;
	}

	protected boolean expression(ParseTree pt){
		//expression = SimpleExpression [("=" |"#"|"<"|"<="|">"|">=") SimpleExpression]
		if(SimpleExpression(pt.setM_Depth(new ParseTree("SimpleExpression")))){
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && ( 
				m_actSym.getElement().equals("=") || 
				m_actSym.getElement().equals("#") ||
				m_actSym.getElement().equals("<") || 
				m_actSym.getElement().equals("<=") || 
				m_actSym.getElement().equals(">") || 
				m_actSym.getElement().equals(">=")
				)){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(SimpleExpression(pt.setM_Depth(new ParseTree("SimpleExpression")))){
				}else{
					OutputError("Synthaxerror SimpleExpression erwartet");
					return false;					
				}
			}
		}else{
			OutputError("Synthaxerror SimpleExpression erwartet");
			return false;
		}
		return true;
	}

	protected boolean SimpleExpression(ParseTree pt){
		//SimpleExpression = ["+"|"-"] term {("+"|"-"|"OR") term}
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && ( 
				m_actSym.getElement().equals("+") || 
				m_actSym.getElement().equals("-")
				)){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
		}
		if(term(pt.setM_Depth(new ParseTree("term")))){
			while(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && ( 
					m_actSym.getElement().equals("+") || 
					m_actSym.getElement().equals("-") ||
					m_actSym.getElement().equals("OR")
					)){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(term(pt.setM_Depth(new ParseTree("term")))){
				}else{
					OutputError("Synthaxerror term erwartet");
					return false;
				}
			}
		}else{
			OutputError("Synthaxerror SimpleExpression erwartet");
			return false;
		}
		return true;
	}
	
	protected boolean term(ParseTree pt){
		//term = factor {("*"|"DIV"|"MOD"|"&") factor}
		if(factor(pt.setM_Depth(new ParseTree("factor")))){
			while(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && ( 
					m_actSym.getElement().equals("*") || 
					m_actSym.getElement().equals("DIV") ||
					m_actSym.getElement().equals("MOD") ||
					m_actSym.getElement().equals("&")
					)){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(factor(pt.setM_Depth(new ParseTree("factor")))){
				}else{
					OutputError("Synthaxerror factor erwartet");
					return false;					
				}
			}
		}else{
			OutputError("Synthaxerror factor erwartet");
			return false;
		}
		return true;
	}

	protected boolean factor(ParseTree pt){
		//factor = ident selector | integer | "(" expression ")" | "~" factor
		if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.IDENTIFIER){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			if(selector(pt.setM_Depth(new ParseTree("selector")))){
			}else{
				OutputError("Synthaxerror selector erwartet");
				return false;			
			}
		}else if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.CONSTANTINT){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
		}else if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("(")){
			pt = pt.setM_Next(new ParseTree(m_actSym));
			m_actSym = m_scn.GetSym();
			if(expression(pt.setM_Depth(new ParseTree("expression")))){
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(")")){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
				}else{
					OutputError("Synthaxerror \")\" erwartet");
					return false;							
				}
			}else if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("~")){
				pt = pt.setM_Next(new ParseTree(m_actSym));
				m_actSym = m_scn.GetSym();
				if(selector(pt.setM_Depth(new ParseTree("selector")))){
				}else{
					OutputError("Synthaxerror factor erwartet");
					return false;							
				}
			}else{
				OutputError("Synthaxerror expression erwartet");
				return false;							
			}
		}else{
			OutputError("Synthaxerror factor erwartet");
			return false;			
		}
		return true;
	}

	protected boolean selector(ParseTree pt){
		//selector = {"." ident | "[" expression "]"}
		while(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && (
				m_actSym.getElement().equals(".") ||
				m_actSym.getElement().equals("[")
				)){
			if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals(".")){
				if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.IDENTIFIER){
					pt = pt.setM_Next(new ParseTree(m_actSym));
					m_actSym = m_scn.GetSym();
				}else{
					OutputError("Synthaxerror ident erwartet");
					return false;
				}			
			}else if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("[")){
				if(expression(pt.setM_Depth(new ParseTree("expression")))){
					if(m_actSym!=null && m_actSym.getType()==Scanner.TokenSym.TOKEN && m_actSym.getElement().equals("]")){
						pt = pt.setM_Next(new ParseTree(m_actSym));
						m_actSym = m_scn.GetSym();						
					}else{
						OutputError("Synthaxerror \"]\" erwartet");
						return false;						
					}
				}else{
					OutputError("Synthaxerror expression erwartet");
					return false;
				}
			}else{
				
			}
		}
		return true;
	}	
}
