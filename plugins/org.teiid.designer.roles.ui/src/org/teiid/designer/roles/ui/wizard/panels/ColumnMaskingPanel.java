/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.roles.ui.wizard.panels;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.roles.Permission;
import org.teiid.designer.roles.ui.Messages;
import org.teiid.designer.roles.ui.RolesUiPlugin;
import org.teiid.designer.roles.ui.wizard.DataRoleWizard;
import org.teiid.designer.roles.ui.wizard.PermissionTreeProvider;
import org.teiid.designer.roles.ui.wizard.dialogs.AbstractAddOrEditTitleDialog;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.widget.MessageLabel;

/**
 *
 */
public class ColumnMaskingPanel extends DataRolePanel {
    private static final char DELIM = CoreStringUtil.Constants.DOT_CHAR;
    private static final char B_SLASH = '/';
    
	TableViewerBuilder tableBuilder;
	Button addButton;
	Button removeButton;
	Button editButton;
	
	PermissionTreeProvider permissionTreeProvider;
	/**
     * @param parent
     * @param wizard
     */
    public ColumnMaskingPanel(Composite parent, DataRoleWizard wizard) {
    	super(parent, wizard);
    }
    

	/* (non-Javadoc)
	 * @see org.teiid.designer.roles.ui.wizard.panels.DataRolePanel#createControl()
	 */
	@Override
	void createControl() {
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(getPrimaryPanel());
		GridDataFactory.fillDefaults().applyTo(getPrimaryPanel());
		
		
		{ // Message/description Text
			Composite thePanel = WidgetFactory.createPanel(getPrimaryPanel(), SWT.NONE, 1, 1);
			GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(thePanel);
			
			Text helpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
			helpText.setBackground(thePanel.getBackground());
			helpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
			helpText.setText(Messages.columnMaskingHelpText);

		}
		
		{
		    tableBuilder = new TableViewerBuilder(getPrimaryPanel(), SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

	        ColumnViewerToolTipSupport.enableFor(this.tableBuilder.getTableViewer());
	        this.tableBuilder.setContentProvider(new IStructuredContentProvider() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	             */
	            @Override
	            public void dispose() {
	                // nothing to do
	            }

	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	             */
	            @Override
	            public Object[] getElements( Object inputElement ) {
	            	List<Permission> permissions =  getWizard().getTreeProvider().getPermissionsWithColumnMasking();

	                if (permissions.isEmpty()) {
	                    return new Object[0];
	                }
	                
	                return permissions.toArray(new Permission[0]);
	            }

	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	             *      java.lang.Object)
	             */
	            @Override
	            public void inputChanged( Viewer viewer,
	                                      Object oldInput,
	                                      Object newInput ) {
	                // nothing to do
	            }
	        });

	        // sort the table rows by display name
	        this.tableBuilder.setComparator(new ViewerComparator() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	             *      java.lang.Object)
	             */
	            @Override
	            public int compare( Viewer viewer,
	                                Object e1,
	                                Object e2 ) {
	            	Permission perm1 = (Permission)e1;
	            	Permission perm2 = (Permission)e2;

	                return super.compare(viewer, perm1.getTargetName(), perm2.getTargetName());
	            }
	        });

	        // create columns
	        TableViewerColumn column = tableBuilder.createColumn(SWT.LEFT, 40, 100, true);
	        column.getColumn().setText(Messages.name + getSpaces(70));
	        column.setLabelProvider(new PermissionLabelProvider(0));
	        
	        column = tableBuilder.createColumn(SWT.LEFT, 20, 100, true);
	        column.getColumn().setText(Messages.condition + getSpaces(70));
	        column.setLabelProvider(new PermissionLabelProvider(1));
	        
	        column = tableBuilder.createColumn(SWT.LEFT, 20, 100, true);
	        column.getColumn().setText(Messages.order);
	        column.setLabelProvider(new PermissionLabelProvider(2));
	        column.setEditingSupport(new OrderEditingSupport(this.tableBuilder.getTableViewer()));

	        column = tableBuilder.createColumn(SWT.LEFT, 20, 100, true);
	        column.getColumn().setText(Messages.mask);
	        column.setLabelProvider(new PermissionLabelProvider(3));

	        this.tableBuilder.addSelectionChangedListener(new ISelectionChangedListener() {
	            /**
	             * {@inheritDoc}
	             * 
	             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	             */
	            @Override
	            public void selectionChanged( SelectionChangedEvent event ) {
	                boolean enable = !tableBuilder.getSelection().isEmpty();
	                editButton.setEnabled(enable);
	                removeButton.setEnabled(enable);

	            }
	        });
	        
	        this.tableBuilder.addDoubleClickListener(new IDoubleClickListener() {
				
				@Override
				public void doubleClick(DoubleClickEvent event) {
					handleEdit();
				}
			});
	        
		}
        
        Composite toolbarPanel = WidgetFactory.createPanel(getPrimaryPanel(), SWT.NONE, GridData.VERTICAL_ALIGN_BEGINNING, 1, 3);
        
        this.addButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.addButton.setText(Messages.add);
        this.addButton.setToolTipText(Messages.addRowBasedSecurityTooltip);
        this.addButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleAdd();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        
        this.editButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.editButton.setText(Messages.edit);
        this.editButton.setToolTipText(Messages.editRowBasedSecurityTooltip);
        this.editButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleEdit();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        this.editButton.setEnabled(false);

        this.removeButton = WidgetFactory.createButton(toolbarPanel, GridData.FILL);
        this.removeButton.setText(Messages.remove);
        this.removeButton.setToolTipText(Messages.removeRowBasedSecurityTooltip);
        this.removeButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRemove();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
        this.removeButton.setEnabled(false);
		
	}
	
	class PermissionLabelProvider extends ColumnLabelProvider {

        private final int columnID;

        public PermissionLabelProvider( int columnID ) {
            this.columnID = columnID;
        }

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			if( element instanceof Permission ) {
				if( columnID == 0 ) {
					return ((Permission)element).getTargetName();
				} else if( columnID == 3 ) {
					return ((Permission)element).getMask();
				}  else if( columnID == 2 ) {
					return Integer.toString(((Permission)element).getOrder());
				} else if( columnID == 1 ) {
					return ((Permission)element).getCondition();
				}
			}
			return super.getText(element);
		}
	}
	
    void handleAdd() {
    	ColumnMaskDialog dialog = new ColumnMaskDialog(getPrimaryPanel().getShell(), 
    			Messages.addColumnMaskTitle, 
                Messages.addColumnMaskMessage,
                null, false);

        if (dialog.open() == Window.OK) {
            // update model
        	String condition = dialog.getCondition();
            String mask = dialog.getMask();
            int order = dialog.getOrder();
            String targetName = dialog.getTargetName();

            getWizard().getTreeProvider().setColumnMask(targetName, condition, mask, order);
            
            getWizard().refreshAllTabs();
        }
    }
    
    void handleEdit() {
    	Permission permission = getSelectedPermission();
    	
    	ColumnMaskDialog dialog = new ColumnMaskDialog(getPrimaryPanel().getShell(), 
    			Messages.editColumnMaskTitle, 
                Messages.editColumnMaskMessage,
                permission, true);

        if (dialog.open() == Window.OK) {
            // update model
            String mask = dialog.getMask();
            int order = dialog.getOrder();
            String condition = dialog.getCondition();
            String targetName = dialog.getTargetName();

            getWizard().getTreeProvider().setColumnMask(targetName, condition, mask, order);
            
            getWizard().refreshAllTabs();
        }
    }
	
    void handleRemove() {
    	Permission selection = getSelectedPermission();
        assert (selection != null);

        // update model
        getWizard().getTreeProvider().removeColumnMask(selection);
        
        getWizard().getTreeProvider().handlePermissionChanged(selection);
        
        // update UI
        getWizard().refreshAllTabs();
    }
    
    private Permission getSelectedPermission() {
        IStructuredSelection selection = (IStructuredSelection)this.tableBuilder.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        return (Permission)selection.getFirstElement();
    }
    
    @Override
    public void refresh() {
        this.tableBuilder.getTable().removeAll();
        for( Permission perm : getWizard().getTreeProvider().getPermissionsWithColumnMasking() ) {
        	this.tableBuilder.add(perm);
        }
        
        if( this.tableBuilder.getSelection().isEmpty() ) {
        	this.editButton.setEnabled(false);
        	this.removeButton.setEnabled(false);
        }
    }
    
    class OrderEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer the column viewer
		 */
		public OrderEditingSupport(ColumnViewer viewer) {
			super(viewer);
			this.editor = new TextCellEditor((Composite) viewer.getControl());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		@Override
		protected Object getValue(Object element) {
			if( element instanceof Permission ) {
				return Integer.toString(((Permission)element).getOrder());
			}
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
			if( element instanceof Permission ) {
				int oldValue = ((Permission)element).getOrder();
				int newValue = oldValue;
				try {
					newValue = Integer.parseInt((String)value);
				} catch (NumberFormatException ex) {
					return;
				}
				if( newValue != oldValue ) {
					((Permission)element).setOrder(newValue);
					tableBuilder.getTableViewer().refresh(element);
				}
			}
		}

	}
    
    class MaskEditingSupport extends EditingSupport {
    	
		private TextCellEditor editor;

		/**
		 * Create a new instance of the receiver.
		 * 
		 * @param viewer the column viewer
		 */
		public MaskEditingSupport(ColumnViewer viewer) {
			super(viewer);
			this.editor = new TextCellEditor((Composite) viewer.getControl());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
		 */
		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
		 */
		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */
		@Override
		protected Object getValue(Object element) {
			if( element instanceof Permission ) {
				return ((Permission)element).getMask();
			}
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 *      java.lang.Object)
		 */
		@Override
		protected void setValue(Object element, Object value) {
			if( element instanceof Permission ) {
				String oldValue = ((Permission)element).getMask();
				String newValue = (String)value;
				if( newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
					((Permission)element).setMask(newValue);
					tableBuilder.getTableViewer().refresh(element);
					handleInfoChanged();
				}
			}
		}

	}
    
    private void handleInfoChanged() {
    	refresh();
    }
    
    protected PermissionTreeProvider getPermissionTreeProvider() {
    	if( permissionTreeProvider == null ) {
    		permissionTreeProvider = new PermissionTreeProvider();
    	}
    	
    	return permissionTreeProvider;
    }
    
    /**
     * This inner class provides for selecting existing language to be allowed for the specified data role
     * The class contains a simple 
     */
    class ColumnMaskDialog extends AbstractAddOrEditTitleDialog {
    	
        private String targetColumn;
        private Text targetColumnText;
        
        private StyledTextEditor conditionTextEditor;
        private String conditionString;
        
        private StyledTextEditor textEditor;
        private String maskString;
        private int order = 0;
        private Text orderText;
        private boolean isEdit;

        /**
         * @param parentShell the parent shell (may be <code>null</code>)
         * @param title
         * @param message
         * @param permission
         * @param okEnabled
         */
        public ColumnMaskDialog( Shell parentShell, String title, String message, Permission permission, boolean okEnabled ) {
            super(parentShell, title, message, okEnabled);

            if( permission != null ) {
            	if( permission.getMask() != null || permission.getCondition() != null) {
	                this.maskString = permission.getMask();
	            	isEdit = true;
	            	this.order = permission.getOrder();
	            	this.targetColumn = permission.getTargetName();
	                this.conditionString = permission.getCondition();
            	}
            }

        }

        
        /**
         * 
         * @param outerPanel
         */
        @Override
		public void createCustomArea( Composite outerPanel ) {
            
    		{
    	        final Composite innerPanel = new Composite(outerPanel, SWT.NONE);
    	        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(innerPanel);
    	        GridDataFactory.fillDefaults().grab(true, false).applyTo(innerPanel);        
    	        
    	        Label theLabel = WidgetFactory.createLabel(innerPanel, Messages.targetColumn);
    	        GridDataFactory.fillDefaults().align(GridData.BEGINNING, GridData.CENTER).applyTo(theLabel);

    	        this.targetColumnText = WidgetFactory.createTextField(innerPanel, GridData.FILL_HORIZONTAL, 1, StringConstants.EMPTY_STRING);
    	        if( isEdit ) {
    	        	this.targetColumnText.setText(this.targetColumn);
    	        }
    	        
    	        this.targetColumnText.addModifyListener(new ModifyListener() {
    	            @Override
    	            public void modifyText( ModifyEvent e ) {
    	            	handleInputChanged();
    	            }
    	        });
    	        this.targetColumnText.setEditable(false);
    	        this.targetColumnText.setBackground(innerPanel.getBackground());
    	        
    	        Button button = new Button(innerPanel, SWT.PUSH);
    	        button.setText(Messages.dotDotDot);
    	        button.setToolTipText(Messages.browseVdbForTargetColumn);
    	        button.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// Open dialog to display models tree so user can select a column object
						handleBrowseForColumn();
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
    	        
    	        final Group group = WidgetFactory.createGroup(innerPanel, Messages.condition, GridData.FILL_HORIZONTAL, 3);
    	        {
    	        	this.conditionTextEditor = new StyledTextEditor(group, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        			GridDataFactory.fillDefaults().grab(true,  true).span(2,  1).applyTo(this.conditionTextEditor.getTextWidget());
        			((GridData)this.conditionTextEditor.getTextWidget().getLayoutData()).heightHint = 50;
        			
        			if( isEdit ) {
        				this.conditionTextEditor.setText(this.conditionString);
        			} else {
        				this.conditionTextEditor.setText(""); //$NON-NLS-1$
        			}
        			this.conditionTextEditor.getDocument().addDocumentListener(new IDocumentListener() {

        	            @Override
        	            public void documentChanged( DocumentEvent event ) {
        	            	handleInputChanged();
        	            }

        	            @Override
        	            public void documentAboutToBeChanged( DocumentEvent event ) {
        	                // NO OP
        	            }
        	        });
    	        }
    	        
    	        Label label = WidgetFactory.createLabel(innerPanel, Messages.order);
    	        label.setToolTipText(Messages.orderTooltip);
    	        GridDataFactory.fillDefaults().align(GridData.BEGINNING, GridData.CENTER).applyTo(label);

    	        this.orderText = WidgetFactory.createTextField(innerPanel, GridData.FILL_HORIZONTAL, 2, StringConstants.EMPTY_STRING);
    	        if( isEdit ) {
    	        	this.orderText.setText(Integer.toString(this.order));
    	        } else {
    	        	this.orderText.setText("0"); //$NON-NLS-1$
    	        }
    	        
    	        this.orderText.addModifyListener(new ModifyListener() {
    	            @Override
    	            public void modifyText( ModifyEvent e ) {
    	            	handleInputChanged();
    	            }
    	        });
    	        
    	        final Group maskGroup = WidgetFactory.createGroup(outerPanel, Messages.columnExpression, GridData.FILL_HORIZONTAL, 1);
    	        {
	    			textEditor = new StyledTextEditor(maskGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
	    			GridDataFactory.fillDefaults().grab(true,  true).span(3,  1).applyTo(textEditor.getTextWidget());
	    			((GridData)textEditor.getTextWidget().getLayoutData()).heightHint = 50;
	    			
	    			if( isEdit ) {
	    				this.textEditor.setText(this.maskString);
	    			} else {
	    				textEditor.setText(""); //$NON-NLS-1$
	    			}
	    	        textEditor.getDocument().addDocumentListener(new IDocumentListener() {
	
	    	            @Override
	    	            public void documentChanged( DocumentEvent event ) {
	    	            	handleInputChanged();
	    	            }
	
	    	            @Override
	    	            public void documentAboutToBeChanged( DocumentEvent event ) {
	    	                // NO OP
	    	            }
	    	        });
    	        }
    	        
    	        { // Column Expression will be used in place of masked column in executed query
    				Composite thePanel = WidgetFactory.createPanel(outerPanel, SWT.NONE, 1, 1);
    				GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(thePanel);
    				GridDataFactory.fillDefaults().grab(true, false).applyTo(thePanel);
    				
    				Text helpText = new Text(thePanel, SWT.WRAP | SWT.READ_ONLY);
    				helpText.setBackground(thePanel.getBackground());
    				helpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
    				helpText.setText(Messages.columnExpressionHelpText);
    	        }

    	        
    		}
        }
        
        /**
         * @return the new targetColumn value (never <code>null</code>)
         * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
         */
        public String getTargetName() {
            CoreArgCheck.isEqual(getReturnCode(), Window.OK);
            return this.targetColumn;
        }

        /**
         * @return the new mask value (never <code>null</code>)
         * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
         */
        public String getMask() {
            CoreArgCheck.isEqual(getReturnCode(), Window.OK);
            return this.maskString;
        }
        
        /**
         * @return the new language (never <code>null</code>)
         * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
         */
        public int getOrder() {
            CoreArgCheck.isEqual(getReturnCode(), Window.OK);
            return order;
        }
        
        /**
         * @return the new condition value (never <code>null</code>)
         * @throws IllegalArgumentException if called when dialog return code is not {@link Window#OK}.
         */
        public String getCondition() {
            CoreArgCheck.isEqual(getReturnCode(), Window.OK);
            return this.conditionString;
        }
        
        
        private void handleBrowseForColumn() {
        	SelectColumnDialog dialog = new SelectColumnDialog(getShell());
        	
            if (dialog.open() == Window.OK) {
            	targetColumn = dialog.getColumnName();
            	targetColumnText.setText(targetColumn);
            	handleInputChanged();
            }
        }


        @Override
        protected void handleInputChanged() {
        	validate();
        }
        
        @Override
        protected void validate() {
        	boolean enable = true;
        	setErrorMessage(null);
        	setMessage(Messages.clickOkToFinish); //Messages.clickOKToFinish);
        	
            maskString = textEditor.getText();
            targetColumn = targetColumnText.getText();
            conditionString = conditionTextEditor.getText();
            
        	boolean conditionEmpty = (this.conditionString == null || this.conditionString.trim().isEmpty());

            boolean maskEmpty = (maskString == null || maskString.trim().isEmpty());
            
            if( targetColumn == null || targetColumn.trim().isEmpty() ) {
            	enable = false;
        		setErrorMessage(Messages.targetColumnUndefined);
        		getButton(IDialogConstants.OK_ID).setEnabled(enable);
        		return;
            }
            
            if( maskEmpty && conditionEmpty) {
            	enable = false;
        		setErrorMessage(Messages.noMaskOrConditionDefined);
        		getButton(IDialogConstants.OK_ID).setEnabled(enable);
        		return;
            }
            
        	if( orderText.getText() != null ) {
        		try {
					order = Integer.parseInt(orderText.getText());
				} catch (NumberFormatException ex) {
					enable = false;
	        		setErrorMessage(Messages.orderMustBeAnInteger);
	        		getButton(IDialogConstants.OK_ID).setEnabled(enable);
	        		return;
				}
        	} else {
        		enable = false;
        		setErrorMessage(Messages.orderMustNotBeNull);
        		getButton(IDialogConstants.OK_ID).setEnabled(enable);
        		return;
        	}
        	
        	// Assume that if order is > 0 (non-default), then the mask cannot be empty
        	if( maskEmpty ) {
        		enable = false;
        		setErrorMessage(Messages.maskIsUndefined);
        		getButton(IDialogConstants.OK_ID).setEnabled(enable);
        		return;
        	}
        	
        	getButton(IDialogConstants.OK_ID).setEnabled(enable);
        }
    }
    
    class SelectColumnDialog extends ElementTreeSelectionDialog implements ISelectionChangedListener {

        private Text columnNameText;
        private String columnName;
        private MessageLabel statusMessageLabel;

        public SelectColumnDialog( Shell parent ) {
            super(parent, getPermissionTreeProvider(), getPermissionTreeProvider());
            setTitle(Messages.targetSelection);
            setMessage(Messages.selectTargetColumn);
            setInput(getWizard().getTempContainer());
            setAllowMultiple(false);
        }

        @Override
        protected Control createDialogArea( Composite parent ) {
            Composite panel = new Composite(parent, SWT.NONE);
            panel.setLayout(new GridLayout());
            GridData panelData = new GridData(GridData.FILL_BOTH);
            panel.setLayoutData(panelData);

            Group selectedGroup = WidgetFactory.createGroup(panel, Messages.selectedColumn, GridData.FILL_HORIZONTAL, 1, 2);

            this.columnNameText = WidgetFactory.createTextField(selectedGroup, GridData.FILL_HORIZONTAL, Messages.undefined);
            GridData data = new GridData(GridData.FILL_HORIZONTAL);
            data.heightHint = convertHeightInCharsToPixels(1);
            this.columnNameText.setLayoutData(data);
            this.columnNameText.setEditable(false);
            this.columnNameText.setBackground(panel.getBackground());
            this.columnNameText.setText(Messages.undefined);

            super.createDialogArea(panel);

            this.statusMessageLabel = new MessageLabel(panel);
            GridData statusData = new GridData(GridData.FILL_HORIZONTAL);
            data.heightHint = convertHeightInCharsToPixels(1);
            this.statusMessageLabel.setLayoutData(statusData);
            this.statusMessageLabel.setEnabled(false);
            this.statusMessageLabel.setText(Messages.undefined);

            getTreeViewer().expandToLevel(2);

            return panel;
        }
        
        /**
         * {@inheritDoc}
         *
         * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createTreeViewer(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected TreeViewer createTreeViewer( Composite parent ) {
            TreeViewer viewer = super.createTreeViewer(parent);
            viewer.addSelectionChangedListener(this);
            viewer.getTree().setEnabled(true);
            viewer.setSorter(new ViewerSorter());
            viewer.setFilters(new ViewerFilter[] { new ViewerFilter() {
                @Override
                public boolean select( Viewer viewer,
                                       Object parentElement,
                                       Object element ) {
                    if (element instanceof EObject || element instanceof Resource) {
                        return true;
                    }

                    return false;
                }
            } });
            
            viewer.setContentProvider(getPermissionTreeProvider());
            viewer.setLabelProvider(getPermissionTreeProvider());

            viewer.setInput(getWizard().getTempContainer());

            return viewer;
        }

        @Override
        public void selectionChanged( SelectionChangedEvent event ) {
            TreeSelection selection = (TreeSelection)event.getSelection();
            if (selection.isEmpty()) {
                this.columnNameText.setText(Messages.undefined);
                this.columnName = null;
                updateOnSelection(null);
                return;
            }

            Object firstElement = selection.getFirstElement();

            if (!(firstElement instanceof Column)) {
                this.columnNameText.setText(Messages.undefined);
                this.columnName = null;
            } else {
                Column column = (Column)selection.getFirstElement();
                columnName = getFullColumnName(column);
                this.columnNameText.setText(column.getName());
            }

            updateOnSelection(firstElement);
        }
        
        private String getFullColumnName(Column column) {
        	String targetName = getResourceName(column.eResource()) + '/' + ModelerCore.getModelEditor().getModelRelativePath(column);

            targetName = targetName.replace(B_SLASH, DELIM);
            
            return targetName;
        }
        
        /*
         * Returns the file name only minus the xmi file extension
         */
        private String getResourceName( Resource res ) {

            if (res.getURI().path().endsWith(".xmi")) { //$NON-NLS-1$
                Path path = new Path(res.getURI().path());
                return path.removeFileExtension().lastSegment();
            }
            return res.getURI().path();
        }

        private void updateOnSelection( Object selectedObject ) {
            IStatus status = new Status(IStatus.INFO,
            		RolesUiPlugin.PLUGIN_ID, Messages.columnMaskingOkMessage);
            if (selectedObject != null) {
                if (!(selectedObject instanceof Column)) {
                    status = new Status(IStatus.ERROR, RolesUiPlugin.PLUGIN_ID, Messages.invalidSelectionColumnMaskingMessage);
                    getOkButton().setEnabled(false);
                } else {
                    getOkButton().setEnabled(true);
                }
            } else {
                status = new Status(IStatus.ERROR, RolesUiPlugin.PLUGIN_ID, Messages.noColumnSelected);                
                getOkButton().setEnabled(false);
            }

            this.statusMessageLabel.setErrorStatus(status);
        }

        /**
         * Returns the current TeiidTranslator
         * 
         * @return the TeiidTranslator. may return null
         */
        public String getColumnName() {
            return this.columnName;
        }

    }
}
