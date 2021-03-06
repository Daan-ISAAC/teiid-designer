/* Generated By:JJTree: Do not edit this line. SubqueryFromClause.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.teiid.query.sql.lang;

import java.util.Collection;
import org.teiid.designer.query.sql.lang.ISubqueryFromClause;
import org.teiid.query.parser.LanguageVisitor;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.parser.TeiidParser;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 *
 */
public class SubqueryFromClause extends FromClause
    implements SubqueryContainer<Command>, ISubqueryFromClause<LanguageVisitor, Command> {

    private GroupSymbol symbol;

    private Command command;

    private boolean table;

    /**
     * @param p
     * @param id
     */
    public SubqueryFromClause(TeiidParser p, int id) {
        super(p, id);
    }

    /**
     * @return table flag
     */
    public boolean isTable() {
        return table;
    }
    
    /**
     * @param table
     */
    public void setTable(boolean table) {
        this.table = table;
    }

    /**
     * Get name of this clause.
     * @return Name of clause
     */
    @Override
    public String getName() {
        return this.symbol.getName();   
    }

    /** 
     * Reset the alias for this subquery from clause and it's pseudo-GroupSymbol.  
     * WARNING: this will modify the hashCode and equals semantics and will cause this object
     * to be lost if currently in a HashMap or HashSet.
     * @param name New name
     * @since 4.3
     */
    @Override
    public void setName(String name) {
        this.symbol = parser.createASTNode(ASTNodes.GROUP_SYMBOL);
        this.symbol.setName(name);
    }

    /**
     * @return groupSymbol
     */
    public GroupSymbol getGroupSymbol() {
        return symbol;
    }

    /**
     * Set the command held by the clause
     * @param command Command to hold
     */
    @Override
    public void setCommand(Command command) {
        this.command = command;
    } 
    
    /**
     * Get command held by clause
     * @return Command held by clause
     */
    @Override
    public Command getCommand() {
        return this.command;
    }   

    @Override
    public void collectGroups(Collection<GroupSymbol> groups) {
        groups.add(getGroupSymbol());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.command == null) ? 0 : this.command.hashCode());
        result = prime * result + ((this.symbol == null) ? 0 : this.symbol.hashCode());
        result = prime * result + (this.table ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        SubqueryFromClause other = (SubqueryFromClause)obj;
        if (this.command == null) {
            if (other.command != null) return false;
        } else if (!this.command.equals(other.command)) return false;
        if (this.getName() == null) {
            if (other.getName() != null) return false;
        } else if (!this.getName().equalsIgnoreCase(getName())) return false;
        if (this.table != other.table) return false;
        return true;
    }

    /** Accept the visitor. **/
    @Override
    public void acceptVisitor(LanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public SubqueryFromClause clone() {
        SubqueryFromClause clone = new SubqueryFromClause(this.parser, this.id);

        if(getCommand() != null)
            clone.setCommand(getCommand().clone());
        clone.setTable(isTable());
        if(getName() != null)
            clone.setName(getName());
        clone.setOptional(isOptional());
        clone.setMakeInd(isMakeInd());
        clone.setNoUnnest(isNoUnnest());
        clone.setMakeDep(isMakeDep());
        clone.setMakeNotDep(isMakeNotDep());
        clone.setPreserve(isPreserve());

        return clone;
    }

}
/* JavaCC - OriginalChecksum=629ba0123099def7e03078d92c74b52d (do not edit this line) */
