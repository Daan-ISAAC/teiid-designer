/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.custom.actions;

import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.metamodels.relationship.Relationship;
import org.teiid.designer.relationship.ui.UiConstants;
import org.teiid.designer.relationship.ui.UiPlugin;
import org.teiid.designer.relationship.ui.actions.RelationshipAction;
import org.teiid.designer.relationship.ui.custom.CustomDiagramModelFactory;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;

/**
 * RemoveTransformationSource
 */
public class RestoreRelationshipAction extends RelationshipAction {
	private DiagramEditor editor;
    
	///////////////////////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	///////////////////////////////////////////////////////////////////////////////////////////////

	public RestoreRelationshipAction() {
		super();
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(UiConstants.Images.RESTORE_RELATIONSHIP));
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	// METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
	 */
	@Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
		super.selectionChanged(thePart, theSelection);

		setEnabled(shouldEnable());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
    protected void doRun() {
		if( editor != null ) {
    
			// Need to get the current diagram
			DiagramModelNode diagramNode = editor.getCurrentModel();
			// Need to get ahold of the CustomDiagramModelFactory
			CustomDiagramModelFactory modelFactory = (CustomDiagramModelFactory)editor.getModelFactory();
			// And call add(SelectionUtilities.getSelectedEObjects(getSelection())
            
			if( diagramNode != null && modelFactory != null ) {
				modelFactory.restoreRelationship(SelectionUtilities.getSelectedEObject(getSelection()), diagramNode);
			}
		}
	}
    
    
	private boolean shouldEnable() {
		if( ! (this.getPart() instanceof ModelEditor) || 
			getSelection() == null ||
			SelectionUtilities.isMultiSelection(getSelection()) ||
			! SelectionUtilities.isSingleSelection(getSelection()) ) {
			return false;
		}
		EObject selectedEObject = SelectionUtilities.getSelectedEObject(getSelection());
		if( !(selectedEObject instanceof Relationship) )
			return false;
			
		return allSelectedInDiagram() && isWritable();
	}
    
	private boolean allSelectedInDiagram() {
		// check the diagram to see if all selected objects are in diagram??
		if( editor != null ) {
			List selectedEObjects = editor.getDiagramViewer().getSelectionHandler().getSelectedEObjects();
			Iterator iter = SelectionUtilities.getSelectedEObjects(getSelection()).iterator();
			EObject eObj = null;
			while( iter.hasNext() ) {
				eObj = (EObject)iter.next();
				if( ! selectedEObjects.contains(eObj))
					return false;
			}
		}
        
		return true;
	}
    
	public void setDiagramEditor(DiagramEditor editor) {
		this.editor = editor;
	}
    
    
	private boolean isWritable() {
		if( editor != null ) {
			DiagramModelNode currentDiagram = editor.getCurrentModel();
			if( currentDiagram != null ) {
				EObject diagram = currentDiagram.getModelObject();
				if( !ModelObjectUtilities.isReadOnly(diagram)) {
					return true;
				}
			}
		}
		return false;
	}
}