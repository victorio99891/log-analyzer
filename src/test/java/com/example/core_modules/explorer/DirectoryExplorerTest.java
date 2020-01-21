package com.example.core_modules.explorer;

import com.example.core_modules.exception.UnsupportedFileFormatException;
import com.example.core_modules.model.file.FilePath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class DirectoryExplorerTest {

    private DirectoryExplorer directoryExplorer;

    private File directory = mock(File.class);
    private File file = mock(File.class);

    @Before
    public void setUp() {
        directoryExplorer = new DirectoryExplorer();
        when(file.isDirectory()).thenReturn(false);
        when(directory.isDirectory()).thenReturn(true);
    }

    @Test
    public void exploreEndDir_directoryNull_shouldThrowNPE() {
        try {
            this.directoryExplorer.exploreEndDir(null);
        } catch (Exception e) {
            Assert.assertThat(e.getCause(), instanceOf(NullPointerException.class));
        }
    }

    @Test
    public void exploreEndDir_directoryNull() {
        try {
            this.directoryExplorer.exploreEndDir(file);
        } catch (Exception e) {
            Assert.assertThat(e.getCause(), instanceOf(UnsupportedFileFormatException.class));
        }
    }


    @Test
    public void exploreEndDir_directoryListFilesNull_shouldReturnEmptySet() {
        when(directory.listFiles()).thenReturn(null);

        Set<FilePath> filePaths = this.directoryExplorer.exploreEndDir(directory);

        Assert.assertTrue(filePaths.isEmpty());
    }


    @Test
    public void exploreEndDir_directoryListFilesMockedFile_shouldReturnEmptySet() {
        when(directory.listFiles()).thenReturn(new File[]{file});
        when(file.isFile()).thenReturn(true);
        when(file.getName()).thenReturn(DirectoryExplorer.ERROR_LOG_FILE_PATTERN);

        Set<FilePath> filePaths = this.directoryExplorer.exploreEndDir(directory);

        Assert.assertEquals(1, filePaths.size());
    }

    @Test
    public void exploreEndDir_directoryListFilesMockedFileTwoTimes_shouldReturnEmptySet() {
        when(directory.listFiles()).thenReturn(new File[]{file});
        when(file.isFile()).thenReturn(false);
        when(file.isDirectory()).thenReturn(true);
        when(file.getName()).thenReturn(DirectoryExplorer.ERROR_LOG_FILE_PATTERN);

        Set<FilePath> filePaths = this.directoryExplorer.exploreEndDir(directory);

        Assert.assertEquals(0, filePaths.size());
    }

}