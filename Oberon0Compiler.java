//written by André Betz 
//http://www.andrebetz.de

import java.io.*;

public class Oberon0Compiler {

	StringBuffer m_File = null;
	Oberon0Compiler(String FileName){
		m_File = Load(FileName);
	}
	public boolean Compile(){
		if(m_File.length()>0){
			Scanner sc = new Scanner(m_File.toString());
			int PosNr = sc.CreateTokenList();
			if(PosNr<0){
				Parser ps = new Parser(sc);
				if(ps.Parse()){
					Parser.ParseTree pt = ps.getParseTree();
					pt.Print();
				}else{
					System.out.println("Parserfehler:" + ps.GetActSymobl().getElement() +" ZeichenNr: "+ ps.GetActSymobl().getElement());
					return false;					
				}
			}else{
				System.out.println("Scanfehler ZeichenNr: " + PosNr);
				return false;
			}
		}
		return true;
	}
	
	protected StringBuffer Load(String FileName){
		StringBuffer readinput = new StringBuffer();
		if(FileName==null){
			return null;
		}
		try {
			File f = new File(FileName);
			FileReader in = new FileReader(f);
			char[] buffer = new char[128];
			int len;
			while((len = in.read(buffer))!=-1) {
				readinput.append(buffer,0,len);
			}
		}
		catch(IOException e) {
			System.out.println("Dateifehler");
		}
		return readinput;  	
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length>0){
			Oberon0Compiler compiler = new Oberon0Compiler(args[0]);
			compiler.Compile();
		}
			
	}

}
