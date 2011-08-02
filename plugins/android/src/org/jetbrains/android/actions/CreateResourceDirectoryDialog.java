package org.jetbrains.android.actions;

import com.android.ide.common.resources.configuration.FolderConfiguration;
import com.android.resources.ResourceFolderType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.android.uipreview.DeviceConfiguratorPanel;
import org.jetbrains.android.uipreview.InvalidOptionValueException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Eugene.Kudelevsky
 */
public abstract class CreateResourceDirectoryDialog extends DialogWrapper {
  private JComboBox myResourceTypeComboBox;
  private JPanel myDeviceConfiguratorWrapper;
  private JTextField myDirectoryNameTextField;
  private JPanel myContentPanel;
  private JBLabel myErrorLabel;

  private final DeviceConfiguratorPanel myDeviceConfiguratorPanel;
  private InputValidator myValidator;

  public CreateResourceDirectoryDialog(@NotNull Project project) {
    super(project);

    myResourceTypeComboBox.setModel(new EnumComboBoxModel<ResourceFolderType>(ResourceFolderType.class));
    myResourceTypeComboBox.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof ResourceFolderType) {
          value = ((ResourceFolderType)value).getName();
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
    });

    myDeviceConfiguratorPanel = new DeviceConfiguratorPanel(null) {
      @Override
      public void applyEditors() {
        try {
          doApplyEditors();
          final FolderConfiguration config = myDeviceConfiguratorPanel.getConfiguration();
          final ResourceFolderType selectedResourceType = (ResourceFolderType)myResourceTypeComboBox.getSelectedItem();
          myDirectoryNameTextField.setText(selectedResourceType != null ? config.getFolderName(selectedResourceType) : "");
          myErrorLabel.setText("");
        }
        catch (InvalidOptionValueException e) {
          myErrorLabel.setText("<html><body><font color=\"red\">" + e.getMessage() + "</font></body></html>");
          myDirectoryNameTextField.setText("");
        }
        setOKActionEnabled(myDirectoryNameTextField.getText().length() > 0);
      }
    };

    myDeviceConfiguratorWrapper.add(myDeviceConfiguratorPanel, BorderLayout.CENTER);
    myResourceTypeComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        myDeviceConfiguratorPanel.applyEditors();
      }
    });

    myDeviceConfiguratorPanel.updateAll();
    setOKActionEnabled(myDirectoryNameTextField.getText().length() > 0);
    init();
  }

  protected abstract InputValidator createValidator();

  @Override
  protected void doOKAction() {
    final String dirName = myDirectoryNameTextField.getText();
    assert dirName != null;
    myValidator = createValidator();
    if (myValidator.checkInput(dirName) && myValidator.canClose(dirName)) {
      super.doOKAction();
    }
  }

  public InputValidator getValidator() {
    return myValidator;
  }

  @Override
  protected JComponent createCenterPanel() {
    return myContentPanel;
  }
}
