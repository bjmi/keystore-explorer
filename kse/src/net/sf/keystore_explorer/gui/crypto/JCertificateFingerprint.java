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
package net.sf.keystore_explorer.gui.crypto;

import static java.awt.Dialog.ModalityType.DOCUMENT_MODAL;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.keystore_explorer.crypto.CryptoException;
import net.sf.keystore_explorer.crypto.digest.DigestType;
import net.sf.keystore_explorer.crypto.digest.DigestUtil;
import net.sf.keystore_explorer.gui.CursorUtil;
import net.sf.keystore_explorer.gui.error.DError;

/**
 * Component to view a fingerprint.
 * 
 */
public class JCertificateFingerprint extends JPanel {
	private static ResourceBundle res = ResourceBundle.getBundle("net/sf/keystore_explorer/gui/crypto/resources");

	private JComboBox jcbFingerprintAlg;
	private JTextField jtfCertificateFingerprint;
	private JButton jbViewCertificateFingerprint;

	private byte[] encodedCertificate;

	/**
	 * Construct a JCertificateFingerprint.
	 * 
	 * @param columns
	 *            Size of text field
	 */
	public JCertificateFingerprint(int columns) {
		initComponents(columns);
	}

	private void initComponents(int columns) {
		jcbFingerprintAlg = new JComboBox();
		jcbFingerprintAlg.setToolTipText(res.getString("JCertificateFingerprint.jcbFingerprintAlg.tooltip"));
		jcbFingerprintAlg.setMaximumRowCount(10);
		jcbFingerprintAlg.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				populateFingerprint();
			}
		});

		GridBagConstraints gbc_jcbFingerprintAlg = new GridBagConstraints();
		gbc_jcbFingerprintAlg.gridwidth = 1;
		gbc_jcbFingerprintAlg.gridheight = 1;
		gbc_jcbFingerprintAlg.gridx = 0;
		gbc_jcbFingerprintAlg.gridy = 0;
		gbc_jcbFingerprintAlg.insets = new Insets(0, 0, 0, 5);

		jtfCertificateFingerprint = new JTextField(columns);
		jtfCertificateFingerprint.setEditable(false);
		jtfCertificateFingerprint.setToolTipText(res
				.getString("JCertificateFingerprint.jtfCertificateFingerprint.tooltip"));

		GridBagConstraints gbc_jtfCertificateFingerprint = new GridBagConstraints();
		gbc_jtfCertificateFingerprint.gridwidth = 1;
		gbc_jtfCertificateFingerprint.gridheight = 1;
		gbc_jtfCertificateFingerprint.gridx = 1;
		gbc_jtfCertificateFingerprint.gridy = 0;
		gbc_jtfCertificateFingerprint.insets = new Insets(0, 0, 0, 5);

		ImageIcon viewIcon = new ImageIcon(getClass().getResource(
				res.getString("JCertificateFingerprint.jbViewCertificateFingerprint.image")));
		jbViewCertificateFingerprint = new JButton(viewIcon);

		jbViewCertificateFingerprint.setToolTipText(res
				.getString("JCertificateFingerprint.jbViewCertificateFingerprint.tooltip"));
		jbViewCertificateFingerprint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					CursorUtil.setCursorBusy(JCertificateFingerprint.this);
					displayFingerprint();
				} finally {
					CursorUtil.setCursorFree(JCertificateFingerprint.this);
				}
			}
		});

		GridBagConstraints gbc_jbViewCertificateFingerprint = new GridBagConstraints();
		gbc_jbViewCertificateFingerprint.gridwidth = 1;
		gbc_jbViewCertificateFingerprint.gridheight = 1;
		gbc_jbViewCertificateFingerprint.gridx = 2;
		gbc_jbViewCertificateFingerprint.gridy = 0;
		gbc_jbViewCertificateFingerprint.insets = new Insets(0, 0, 0, 0);

		setLayout(new GridBagLayout());
		add(jcbFingerprintAlg, gbc_jcbFingerprintAlg);
		add(jtfCertificateFingerprint, gbc_jtfCertificateFingerprint);
		add(jbViewCertificateFingerprint, gbc_jbViewCertificateFingerprint);

		populateFingerprintAlgs();
		populateFingerprint();
	}

	private void populateFingerprintAlgs() {
		DigestType[] digestAlgs = DigestType.values();

		for (DigestType digestAlg : digestAlgs) {
			jcbFingerprintAlg.addItem(digestAlg);
		}

		jcbFingerprintAlg.setSelectedIndex(0);
	}

	/**
	 * Set encoded certificate.
	 * 
	 * @param encodedCertificate
	 *            Encoded certificate
	 */
	public void setEncodedCertificate(byte[] encodedCertificate) {
		this.encodedCertificate = encodedCertificate;
		populateFingerprint();
	}

	public void setFingerprintAlg(DigestType fingerprintAlg) {
		jcbFingerprintAlg.setSelectedItem(fingerprintAlg);
	}

	public DigestType getSelectedFingerprintAlg() {
		return (DigestType) jcbFingerprintAlg.getSelectedItem();
	}

	/**
	 * Sets whether or not the component is enabled.
	 * 
	 * @param enabled
	 *            True if this component should be enabled, false otherwise
	 */
	public void setEnabled(boolean enabled) {
		jbViewCertificateFingerprint.setEnabled(enabled);
	}

	private void populateFingerprint() {
		if (encodedCertificate != null) {
			DigestType fingerprintAlg = (DigestType) jcbFingerprintAlg.getSelectedItem();

			try {
				jtfCertificateFingerprint.setText(DigestUtil.getFriendlyMessageDigest(encodedCertificate,
						fingerprintAlg));
			} catch (CryptoException ex) {
				Container container = getTopLevelAncestor();

				DError dError = null;

				if (container instanceof JDialog) {
					dError = new DError((JDialog) container, DOCUMENT_MODAL, ex);
				} else if (container instanceof JFrame) {
					dError = new DError((JFrame) container, DOCUMENT_MODAL, ex);
				}

				dError.setLocationRelativeTo(container);
				dError.setVisible(true);
				return;
			}
		} else {
			jtfCertificateFingerprint.setText("");
		}

		jtfCertificateFingerprint.setCaretPosition(0);
	}

	private void displayFingerprint() {
		Container container = getTopLevelAncestor();

		DigestType fingerprintAlg = (DigestType) jcbFingerprintAlg.getSelectedItem();

		if (container instanceof JDialog) {
			DViewCertificateFingerprint dViewCertificateFingerprint = new DViewCertificateFingerprint(
					(JDialog) container, encodedCertificate, fingerprintAlg);
			dViewCertificateFingerprint.setLocationRelativeTo(container);
			dViewCertificateFingerprint.setVisible(true);
		} else if (container instanceof JFrame) {
			DViewCertificateFingerprint dViewCertificateFingerprint = new DViewCertificateFingerprint(
					(JFrame) container, encodedCertificate, fingerprintAlg);
			dViewCertificateFingerprint.setLocationRelativeTo(container);
			dViewCertificateFingerprint.setVisible(true);
		}
	}
}
