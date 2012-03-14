/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.panels;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ColumnInfo;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ProcedureInfo;

import com.metamatrix.ui.internal.util.WidgetFactory;

public class ElementsInfoPanel {
	private ProcedureInfo procedureInfo;
	private Button addButton, deleteButton, upButton, downButton;
	EditElementsPanel editElementsPanel;
	private int type = -1;

	final OperationsDetailsPage detailsPage;

	public ElementsInfoPanel(Composite parent, int style, int type, OperationsDetailsPage detailsPage) {
		super();
		this.type = type;
		this.detailsPage = detailsPage;
		init(parent);
	}

	public ProcedureInfo getProcedureInfo() {
		return this.procedureInfo;
	}

	public void setProcedureInfo(ProcedureInfo info) {
		this.procedureInfo = info;
		editElementsPanel.setProcedureInfo(info);
		editElementsPanel.refresh();
		this.addButton.setEnabled(info != null);
	}

	public void refresh() {
		this.editElementsPanel.refresh();
	}

	private void init(Composite parent) {
		Group columnInfoGroup = WidgetFactory.createGroup(parent, Messages.ElementInfo, SWT.NONE, 2);
		columnInfoGroup.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		columnInfoGroup.setLayoutData(gd);

		Composite leftToolbarPanel = new Composite(columnInfoGroup, SWT.NONE);
		GridLayout tbGL = new GridLayout();
		tbGL.marginHeight = 0;
		tbGL.marginWidth = 0;
		tbGL.verticalSpacing = 2;
		leftToolbarPanel.setLayout(tbGL);
		GridData ltpGD = new GridData(GridData.FILL_VERTICAL);
		ltpGD.heightHint = 120;

		leftToolbarPanel.setLayoutData(ltpGD);

		addButton = new Button(leftToolbarPanel, SWT.PUSH);
		addButton.setText(Messages.Add);
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.setEnabled(false);
		addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Check Selection from tree
				if (!detailsPage.createRequestColumn()) {
					String newName = "column_" + (detailsPage.getProcedureGenerator().getRequestInfo().getColumnInfoList().length + 1); //$NON-NLS-1$
					detailsPage.getProcedureGenerator().getRequestInfo().addColumn(newName, false, ColumnInfo.DEFAULT_DATATYPE, null, null);
					editElementsPanel.refresh();
					notifyColumnDataChanged();
				}
			}

		});

		deleteButton = new Button(leftToolbarPanel, SWT.PUSH);
		deleteButton.setText(Messages.Delete);
		deleteButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		deleteButton.setEnabled(false);
		deleteButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColumnInfo info = editElementsPanel.getSelectedColumn();
				if (info != null) {
					detailsPage.getProcedureGenerator().getRequestInfo().removeColumn(info);

					deleteButton.setEnabled(false);
					editElementsPanel.selectRow(-1);
					editElementsPanel.refresh();
					notifyColumnDataChanged();
				}
			}

		});

		upButton = new Button(leftToolbarPanel, SWT.PUSH);
		upButton.setText(Messages.Up);
		upButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		upButton.setEnabled(false);
		upButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColumnInfo info = editElementsPanel.getSelectedColumn();
				if (info != null) {
					int selectedIndex = editElementsPanel.getSelectedIndex();
					detailsPage.getProcedureGenerator().getRequestInfo().moveColumnUp(info);
					downButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveDown(info));
					upButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveUp(info));
					editElementsPanel.refresh();
					notifyColumnDataChanged();

					editElementsPanel.selectRow(selectedIndex - 1);
				}
			}

		});

		downButton = new Button(leftToolbarPanel, SWT.PUSH);
		downButton.setText(Messages.Down);
		downButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		downButton.setEnabled(false);
		downButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColumnInfo info = editElementsPanel.getSelectedColumn();
				if (info != null) {
					int selectedIndex = editElementsPanel.getSelectedIndex();
					detailsPage.getProcedureGenerator().getRequestInfo().moveColumnDown(info);
					downButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveDown(info));
					upButton.setEnabled(detailsPage.getProcedureGenerator().getRequestInfo().canMoveUp(info));
					editElementsPanel.refresh();
					notifyColumnDataChanged();

					editElementsPanel.selectRow(selectedIndex + 1);
				}
			}

		});

		editElementsPanel = new EditElementsPanel(columnInfoGroup, SWT.NONE, this.type, this.detailsPage);

		editElementsPanel.addSelectionListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();

				if (sel.isEmpty()) {
					deleteButton.setEnabled(false);
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				} else {
					boolean enable = true;
					Object[] objs = sel.toArray();
					ColumnInfo columnInfo = null;
					for (Object obj : objs) {
						if (!(obj instanceof ColumnInfo)) {
							enable = false;
							break;
						} else {
							columnInfo = (ColumnInfo) obj;
						}
					}
					if (objs.length == 0) {
						enable = false;
					}
					deleteButton.setEnabled(enable);
					if (enable) {
						upButton.setEnabled(procedureInfo.canMoveUp(columnInfo));
						downButton.setEnabled(procedureInfo.canMoveDown(columnInfo));
					}

				}

			}
		});
	}

	private void notifyColumnDataChanged() {
		this.detailsPage.notifyColumnDataChanged();
	}

}