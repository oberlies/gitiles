package com.google.gitiles.doc;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.eclipse.jgit.internal.storage.dfs.DfsRepository;
import org.eclipse.jgit.junit.TestRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.gitiles.ServletTest;

@RunWith(JUnit4.class)
public class MarkdownRenderingTest extends ServletTest {

	private File workDir = new File("src/test/resources/MarkdownRenderingTest/");
	private TestRepository<DfsRepository>.CommitBuilder commit;

	@Test
	public void commonmarkCompliance() throws Exception {
		commit = repo.branch("master").commit();

		List<File> files = Arrays.asList(workDir.listFiles());
		files.stream().filter(file -> file.isFile()).filter(file -> !file.getName().endsWith(".html")).forEach(file -> {
				try {
					commit = commit.add(file.getName(), repo.blob(Files.readAllBytes(file.toPath())));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		commit.create();

		files.stream().map(file -> file.getName())
				.filter(fileName -> fileName.toLowerCase(Locale.ENGLISH).endsWith(".md")).forEach(fileName -> {
					try {
						String html = buildHtml("/repo/+doc/master/" + fileName);
						Files.write(new File(workDir, fileName.substring(0, fileName.length() - 3) + ".html").toPath(),
								html.getBytes());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
	}

}
