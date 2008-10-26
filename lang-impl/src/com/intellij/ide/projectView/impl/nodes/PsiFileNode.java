package com.intellij.ide.projectView.impl.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class PsiFileNode extends BasePsiNode<PsiFile>{

  public PsiFileNode(Project project, PsiFile value, ViewSettings viewSettings) {
    super(project, value, viewSettings);
  }

  public Collection<AbstractTreeNode> getChildrenImpl() {
    return new ArrayList<AbstractTreeNode>();
  }

  protected void updateImpl(PresentationData data) {
    final PsiFile value = getValue();
    data.setPresentableText(value.getName());
    data.setIcons(value.getIcon(Iconable.ICON_FLAG_READ_STATUS));
  }

  public VirtualFile getVirtualFile() {
    return getValue().getVirtualFile();
  }

  public int getWeight() {
    return 20;
  }

  @Override
  public String getTitle() {
    final PsiFile file = getValue();
    if (file != null) {
      return file.getVirtualFile().getPresentableUrl();
    }
    return super.getTitle();
  }

  @Override
  protected boolean isMarkReadOnly() {
    return true;
  }

  public Comparable getTypeSortKey() {
    String extension = extension(getValue());
    return extension == null ? null : new ExtensionSortKey(extension);
  }

  @Nullable
  public static String extension(final PsiFile file) {
    return file == null || file.getVirtualFile() == null ? null : file.getVirtualFile().getFileType().getDefaultExtension();
  }

  public static class ExtensionSortKey implements Comparable {
    private String myExtension;

    public ExtensionSortKey(final String extension) {
      myExtension = extension;
    }

    public int compareTo(final Object o) {
      if (!(o instanceof ExtensionSortKey)) return 0;
      ExtensionSortKey rhs = (ExtensionSortKey) o;
      return myExtension.compareTo(rhs.myExtension);
    }
  }

  @Override
  public boolean shouldDrillDownOnEmptyElement() {
    final PsiFile file = getValue();
    return file != null && file.getFileType() == StdFileTypes.JAVA;
  }

  public boolean canRepresent(final Object element) {
    return super.canRepresent(element) || getValue().getVirtualFile() == element;
  }
}
