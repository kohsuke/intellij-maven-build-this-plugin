package org.kohsuke.idea.mbt;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.state.MavenProjectsState;
import org.jetbrains.idea.maven.runner.executor.MavenRunnerParameters;
import org.jetbrains.idea.maven.runner.MavenRunner;

import java.util.Arrays;

/**
 * Builds the current project.
 *
 * @author Kohsuke Kawaguchi
 */
public class BuildThisAction extends AnAction {
    public BuildThisAction() {
    }

    public void actionPerformed(AnActionEvent event) {
        Project p = DataKeys.PROJECT.getData(event.getDataContext());

        MavenRunnerParameters params = new MavenRunnerParameters();
        params.setGoals(Arrays.asList("install"));

        VirtualFile pom = findPom(event);

        params.setPomPath(pom.getPath());

        MavenProjectsState state = p.getComponent(MavenProjectsState.class);
        params.setProfiles(state.getProfiles(pom));

        MavenRunner builder = p.getComponent(MavenRunner.class);
        builder.run(params);
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
