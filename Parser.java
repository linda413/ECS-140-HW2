/* *** This file is given as part of the programming assignment. *** */

public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private void scan() {
	tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
	this.scanner = scanner;
	scan();
	program();
	if( tok.kind != TK.EOF )
	    parse_error("junk after logical end of program");
    }

    private void program() {
	block();
    }

    private void block(){
	declaration_list();
	statement_list();
    }

    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
	while( is(TK.DECLARE) ) {
	    declaration();
	}
    }

    private void declaration() {
	mustbe(TK.DECLARE);
	mustbe(TK.ID);
	while( is(TK.COMMA) ) {
		mustbe(TK.COMMA);
	    mustbe(TK.ID);
	}
    }

    private void statement_list() {

    	while ( is(TK.PRINT)| is(TK.DO)|is(TK.IF)| is(TK.ID)| is(TK.TILDE)) {
			statement();

		}
    }

	private void statement(){
		if ( is(TK.PRINT)){
			//then print statement
			print();
		}
		else if ( is(TK.DO)){
			do_();
		}
		else if (is(TK.IF)){
			if_();
		}
		else if (is(TK.TILDE)|is(TK.ID)){
			assign();
		}
	}
	private void assign(){
    	if(is(TK.TILDE)){
    		mustbe(TK.TILDE);
		}
		if (is(TK.NUM)){
			mustbe(TK.NUM);
		}
    	mustbe(TK.ID);
    	mustbe(TK.ASSIGN);
    	expression();
	}

	private void do_(){
		mustbe(TK.DO);
		guarded_Command();
		mustbe(TK.ENDDO);
	}
	private void if_(){
    	mustbe(TK.IF);
    	guarded_Command();
    	while(is(TK.ELSEIF)){
    		mustbe(TK.ELSEIF);
    		guarded_Command();
		}
		if ( is(TK.ELSE) ){
			mustbe(TK.ELSE);
			block();
		}
		mustbe(TK.ENDIF);
	}

	private void guarded_Command(){
		expression();
		mustbe(TK.THEN);
		block();
	}

	private void print(){
    	mustbe(TK.PRINT);
    	expression();
	}

	private void expression(){
    	term();
    	while ( is(TK.MINUS)| is(TK.PLUS) ){
			scan();
			term();
		}
	}
	private void term(){
    	factor();
		while ( is(TK.TIMES)| is(TK.DIVIDE)){
			scan();
			factor();
		}
	}

	private void factor(){
    	if(is(TK.LPAREN)){
    		mustbe(TK.LPAREN);
    		expression();
    		mustbe(TK.RPAREN);
		}
		else if (is(TK.TILDE)){
			mustbe(TK.TILDE);
			if (is(TK.NUM)){
				mustbe(TK.NUM);
			}

			mustbe(TK.ID);
		}
    	else if ( is(TK.NUM) |is(TK.ID)){
			scan();
		}
		else{
			parse_error("factor error");
		}
	}



    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
	if( tok.kind != tk ) {
	    System.err.println( "mustbe: want " + tk + ", got " +
				    tok);
	    parse_error( "missing token (mustbe)" );
	}
	scan();
    }

    private void parse_error(String msg) {
	System.err.println( "can't parse: line "
			    + tok.lineNumber + " " + msg );
	System.exit(1);
    }
}
