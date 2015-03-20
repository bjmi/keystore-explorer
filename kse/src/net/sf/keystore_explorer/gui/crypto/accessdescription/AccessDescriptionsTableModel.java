/*
 * Copyright 2004 - 2013 Wayne Grant
 *           2013 - 2015 Kai Kramer
 *
 * This file is part of KeyStore Explorer.
 *
 * KeyStore Explorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * KeyStore Explorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KeyStore Explorer.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.keystore_explorer.gui.crypto.accessdescription;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import net.sf.keystore_explorer.crypto.x509.GeneralNameUtil;
import net.sf.keystore_explorer.utilities.oid.ObjectIdComparator;

import org.bouncycastle.asn1.x509.AccessDescription;

/**
 * The table model used to display access descriptions.
 * 
 */
public class AccessDescriptionsTableModel extends AbstractTableModel {
	private static ResourceBundle res = ResourceBundle
			.getBundle("net/sf/keystore_explorer/gui/crypto/accessdescription/resources");
	private static ObjectIdComparator objectIdComparator = new ObjectIdComparator();

	private String[] columnNames;
	private Object[][] data;

	/**
	 * Construct a new AccessDescriptionsTableModel.
	 */
	public AccessDescriptionsTableModel() {
		columnNames = new String[2];
		columnNames[0] = res.getString("AccessDescriptionsTableModel.AccessMethodColumn");
		columnNames[1] = res.getString("AccessDescriptionsTableModel.AccessLocationColumn");

		data = new Object[0][0];
	}

	/**
	 * Load the AccessDescriptionsTableModel with access descriptions.
	 * 
	 * @param accessDescriptions
	 *            The access descriptions
	 */
	public void load(List<AccessDescription> accessDescriptions) {
		AccessDescription[] accessDescriptionsArray = accessDescriptions
				.toArray(new AccessDescription[accessDescriptions.size()]);
		Arrays.sort(accessDescriptionsArray, new AccessDescriptionMethodComparator());

		data = new Object[accessDescriptionsArray.length][2];

		int i = 0;
		for (AccessDescription accessDescription : accessDescriptionsArray) {
			data[i][0] = accessDescription;
			data[i][1] = accessDescription;
			i++;
		}

		fireTableDataChanged();
	}

	/**
	 * Get the number of columns in the table.
	 * 
	 * @return The number of columns
	 */
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * Get the number of rows in the table.
	 * 
	 * @return The number of rows
	 */
	public int getRowCount() {
		return data.length;
	}

	/**
	 * Get the name of the column at the given position.
	 * 
	 * @param col
	 *            The column position
	 * @return The column name
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * Get the cell value at the given row and column position.
	 * 
	 * @param row
	 *            The row position
	 * @param col
	 *            The column position
	 * @return The cell value
	 */
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	/**
	 * Get the class at of the cells at the given column position.
	 * 
	 * @param col
	 *            The column position
	 * @return The column cells' class
	 */
	public Class<?> getColumnClass(int col) {
		return AccessDescription.class;
	}

	/**
	 * Is the cell at the given row and column position editable?
	 * 
	 * @param row
	 *            The row position
	 * @param col
	 *            The column position
	 * @return True if the cell is editable, false otherwise
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	static class AccessDescriptionMethodComparator implements Comparator<AccessDescription> {
		public int compare(AccessDescription description1, AccessDescription description2) {
			return objectIdComparator.compare(description1.getAccessMethod(), description2.getAccessMethod());
		}
	}

	static class AccessDescriptionLocationComparator implements Comparator<AccessDescription> {
		public int compare(AccessDescription description1, AccessDescription description2) {
			return GeneralNameUtil.safeToString(description1.getAccessLocation()).compareToIgnoreCase(
					GeneralNameUtil.safeToString(description2.getAccessLocation()));
		}
	}
}
