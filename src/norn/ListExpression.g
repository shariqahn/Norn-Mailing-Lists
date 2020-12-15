// Grammar for Norn mailing list expressions
@skip whitespace {
    listexpression ::= sequence ("|" sequence)*;
    sequence ::= listdefinition (';' listdefinition)*;
    listdefinition::=listname ("=" union)*| union;
    union::= difference(',' (difference|listdefinition))*;//difference(',' difference)*
    
    difference ::= intersection ('!' intersection)*;
    intersection ::= primitive ('*' primitive)*;
    primitive ::= email | listname  |empty|'(' listexpression ')';
    email ::= username "@" domain;
    
}
empty ::="";
listname ::=[A-Za-z0-9._-][A-Za-z0-9._-]*;
username::= [A-Za-z0-9+._-][A-Za-z0-9+._-]*;//letters, digits, underscores, dashes, periods, and plus signs
domain::=[A-Za-z0-9._-][A-Za-z0-9._-]*;
whitespace ::= [ \t\r\n]+;
