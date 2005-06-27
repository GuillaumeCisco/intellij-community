/**
 * @author Alexey
 */
package com.intellij.lang.properties;

import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.project.Project;
import com.intellij.refactoring.rename.RenameHandlerRegistry;
import com.intellij.util.SmartList;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ResourceBundleImpl implements ResourceBundle {
  private final @NotNull VirtualFile myBaseDirectory;
  private final @NotNull String myBaseName;

  public ResourceBundleImpl(@NotNull VirtualFile baseDirectory, @NotNull String baseName) {
    myBaseDirectory = baseDirectory;
    myBaseName = baseName;
  }

  static {
    RenameHandlerRegistry.getInstance().registerHandler(ResourceBundleRenameHandler.INSTANCE);
  }

  public List<PropertiesFile> getPropertiesFiles(final Project project) {
    VirtualFile[] children = myBaseDirectory.getChildren();
    List<PropertiesFile> result = new SmartList<PropertiesFile>();
    FileTypeManager fileTypeManager = FileTypeManager.getInstance();
    PsiManager psiManager = PsiManager.getInstance(project);
    for (VirtualFile file : children) {
      if (fileTypeManager.getFileTypeByFile(file) != StdFileTypes.PROPERTIES) continue;
      if (Comparing.strEqual(PropertiesUtil.getBaseName(file), myBaseName)) {
        PsiFile psiFile = psiManager.findFile(file);
        if (psiFile instanceof PropertiesFile) {
          result.add((PropertiesFile)psiFile);
        }
      }
    }
    return result;
  }

  public String getBaseName() {
    return myBaseName;
  }

  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final ResourceBundleImpl resourceBundle = (ResourceBundleImpl)o;

    if (!myBaseDirectory.equals(resourceBundle.myBaseDirectory)) return false;
    if (!myBaseName.equals(resourceBundle.myBaseName)) return false;

    return true;
  }

  public int hashCode() {
    int result;
    result = myBaseDirectory.hashCode();
    result = 29 * result + myBaseName.hashCode();
    return result;
  }

  public static ResourceBundle createByUrl(String url) {
    if (!url.startsWith("resourceBundle:")) return null;

    String defaultPropertiesUrl = url.substring("resourceBundle:".length());
    VirtualFile defaultProperties = VirtualFileManager.getInstance().findFileByUrl(defaultPropertiesUrl);
    if (defaultProperties != null && FileTypeManager.getInstance().getFileTypeByFile(defaultProperties) == StdFileTypes.PROPERTIES) {
      ResourceBundleImpl resourceBundle = new ResourceBundleImpl(defaultProperties.getParent(),
                                                                 PropertiesUtil.getBaseName(defaultProperties));
      return resourceBundle;

    }
    return null;
  }

  public String getUrl() {
    return "resourceBundle:"+getBaseName();
  }
}