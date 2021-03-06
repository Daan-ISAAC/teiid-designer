/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Logical Relationship</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.relational.LogicalRelationship#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.LogicalRelationship#getSchema <em>Schema</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.LogicalRelationship#getEnds <em>Ends</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.relational.RelationalPackage#getLogicalRelationship()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface LogicalRelationship extends Relationship{
    /**
     * Returns the value of the '<em><b>Catalog</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Catalog#getLogicalRelationships <em>Logical Relationships</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Catalog</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Catalog</em>' container reference.
     * @see #setCatalog(Catalog)
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getLogicalRelationship_Catalog()
     * @see org.teiid.designer.metamodels.relational.Catalog#getLogicalRelationships
     * @model opposite="logicalRelationships"
     * @generated
     */
    Catalog getCatalog();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relational.LogicalRelationship#getCatalog <em>Catalog</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Catalog</em>' container reference.
     * @see #getCatalog()
     * @generated
     */
    void setCatalog(Catalog value);

    /**
     * Returns the value of the '<em><b>Schema</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Schema#getLogicalRelationships <em>Logical Relationships</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Schema</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Schema</em>' container reference.
     * @see #setSchema(Schema)
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getLogicalRelationship_Schema()
     * @see org.teiid.designer.metamodels.relational.Schema#getLogicalRelationships
     * @model opposite="logicalRelationships"
     * @generated
     */
    Schema getSchema();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relational.LogicalRelationship#getSchema <em>Schema</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Schema</em>' container reference.
     * @see #getSchema()
     * @generated
     */
    void setSchema(Schema value);

    /**
     * Returns the value of the '<em><b>Ends</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.LogicalRelationshipEnd}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.LogicalRelationshipEnd#getRelationship <em>Relationship</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ends</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Ends</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getLogicalRelationship_Ends()
     * @see org.teiid.designer.metamodels.relational.LogicalRelationshipEnd#getRelationship
     * @model type="org.teiid.designer.metamodels.relational.LogicalRelationshipEnd" opposite="relationship" containment="true" lower="2"
     * @generated
     */
    EList getEnds();

} // LogicalRelationship
