/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.custom.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.actions.DiagramAction;
import org.teiid.designer.diagram.ui.custom.CustomDiagramModelFactory;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.diagram.ui.model.DiagramModelNode;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;


/**
 * ClearTransformation
 *
 * @since 8.0
 */
public class ClearDiagramAction extends DiagramAction {
	private static final String THIS_CLASS = "ClearDiagramAction"; //$NON-NLS-1$
    private DiagramEditor editor;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public ClearDiagramAction() {
        super();
        setImageDescriptor(DiagramUiPlugin.getDefault().getImageDescriptor(DiagramUiConstants.Images.CLEAR_DIAGRAM));
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
				boolean requiredStart = false;
				boolean succeeded = false;
				try {
					//------------------------------------------------- 
					// Let's wrap this in a transaction!!! 
					//------------------------------------------------- 

					requiredStart = ModelerCore.startTxn(true, false, "Add To Custom Diagram", this); //$NON-NLS-1$$
    
					modelFactory.clear(diagramNode);
					
					succeeded = true;
				}  catch (Exception ex){
					DiagramUiConstants.Util.log(IStatus.ERROR, ex, ex.getClass().getName() + ":" +  THIS_CLASS + ".doRun()"); //$NON-NLS-1$  //$NON-NLS-2$
				} finally {
					if(requiredStart){
						if ( succeeded ) {
							ModelerCore.commitTxn( );
						} else {
							ModelerCore.rollbackTxn( );
						}
					}
				}
			}
        }
    }
    
    
    private boolean shouldEnable() {
        return diagramNotEmpty() && isWritable();
    }
    
    public void setDiagramEditor(DiagramEditor editor) {
        this.editor = editor;
    }
    
    private boolean diagramNotEmpty() {
        if( editor != null ) {
            DiagramModelNode currentDiagram = editor.getCurrentModel();
            if( currentDiagram != null && currentDiagram.getChildren() != null && 
                ! currentDiagram.getChildren().isEmpty() &&
                currentDiagram.getChildren().size() > 0 )
                return true;
        }
            
        return false;
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
