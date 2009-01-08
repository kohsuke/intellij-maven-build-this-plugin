package org.kohsuke.idea.mbt;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.runner.MavenRunConfigurationType;
import org.jetbrains.idea.maven.runner.MavenRunnerParameters;

import java.util.Collections;

/**
 * Builds the current module.
 *
 * @author Kohsuke Kawaguchi
 */
public class BuildThisAction extends AnAction {
    public BuildThisAction() {
    }

    public void actionPerformed(AnActionEvent event) {
        Project p = DataKeys.PROJECT.getData(event.getDataContext());
        VirtualFile pom = findPom(event);

        MavenRunnerParameters params = new MavenRunnerParameters(
                true /*isPomExecution*/,
                pom.getParent().getPath() /*working dir*/,
                Collections.singletonList("install") /*goal*/,
                MavenProjectsManager.getInstance(p).getActiveProfiles() /*profiles*/
        );
        try {
            MavenRunConfigurationType.runConfiguration(p, params, event.getDataContext());
        } catch (ExecutionException e) {
            throw new Error(e); // don't know when this happens
        }
    }

    public void update(AnActionEvent event) {
        Presentation p = event.getPresentation();
        p.setEnabled(findPom(event)!=null);
    }
    
    private VirtualFile findPom(AnActionEvent event) {
// getModuleFile() returned null.
//        VirtualFile pom = m.getModuleFile().getParent().findChild("pom.xml");

        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
        return findPom(file);
    }

    private VirtualFile findPom(VirtualFile f) {
        if(f==null) return null;
        VirtualFile pom = f.findChild("pom.xml");
        if(pom!=null)   return pom;
        return findPom(f.getParent());
    }
}
