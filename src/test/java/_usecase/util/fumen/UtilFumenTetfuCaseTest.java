package _usecase.util.fumen;

import _usecase.Log;
import _usecase.RunnerHelper;
import _usecase.path.files.OutputFileHelper;
import entry.EntryPointMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class UtilFumenTetfuCaseTest {
    private String buildCommand(String subCommand, String fumen, String options) {
        return String.format("util fumen -M %s -t %s %s", subCommand, fumen, options);
    }

    @Nested
    class FumenTest extends UtilFumenUseCaseBaseTest {
        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void noMode() throws Exception {
            String fumen = "v115@vhdXKJNJJ0/ISSJzHJGDJJHJSGJvLJ0KJJEJzJJ+NJ?tMJFDJz/IyQJsGJOGJpFJ+NJ3MJXDJULJzGJxBJiJJtKJyJ?J0JJ";
            String command = String.format("util fumen -t %s", fumen);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getReturnCode()).isEqualTo(1);

            assertThat(log.getError())
                    .contains(ErrorMessages.failPreMain())
                    .contains("Should specify mode");

            assertThat(OutputFileHelper.existsErrorText()).isTrue();

            String errorFile = OutputFileHelper.loadErrorText();
            assertThat(errorFile)
                    .contains(command)
                    .contains("Should specify mode [FinderParseException]");
        }

        @Test
        void invlidMode() throws Exception {
            String fumen = "v115@vhdXKJNJJ0/ISSJzHJGDJJHJSGJvLJ0KJJEJzJJ+NJ?tMJFDJz/IyQJsGJOGJpFJ+NJ3MJXDJULJzGJxBJiJJtKJyJ?J0JJ";
            String command = String.format("util fumen -M invalid -t %s", fumen);
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getReturnCode()).isEqualTo(1);

            assertThat(log.getError())
                    .contains(ErrorMessages.failPreMain())
                    .contains("Unsupported mode: mode=invalid");

            assertThat(OutputFileHelper.existsErrorText()).isTrue();

            String errorFile = OutputFileHelper.loadErrorText();
            assertThat(errorFile)
                    .contains(command)
                    .contains("Unsupported mode: mode=invalid [FinderParseException]");
        }
    }

    @Nested
    class FumenReduceTest extends UtilFumenUseCaseBaseTest {
        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void reduce1() throws Exception {
            String fumen = "v115@vhdXKJNJJ0/ISSJzHJGDJJHJSGJvLJ0KJJEJzJJ+NJ?tMJFDJz/IyQJsGJOGJpFJ+NJ3MJXDJULJzGJxBJiJJtKJyJ?J0JJ";
            String command = buildCommand("reduce", fumen, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput().trim()).isEqualTo("v115@tfBtglwwzhR4ilxwRpR4g0ilBtRpR4g0glBtwwBtR4?h0whRpwhh0AtywwhRpwhg0Btxwg0whRpwhg0Atglxwg0whR?pwhilwwh0DtQ4glwhi0wwBtilwhRpg0xwT4whRpglwwR4Bt?Q4whilJeAgH");
        }

        @Test
        void reduce2Over() throws Exception {
            // フィールドの高さが24以上必要なケース
            String fumen = "v115@xeI8AeI8AeI8AeI8AeI8AeI8AeI8AeI8AeI8AeI8Ae?I8AeI8AeI8AeI8AeI8AeI8AeI8AeI8Key0HvhSTqHmlH0yH?9zHXpHprH3sH22Hx2HJqHzvHS2HXsHzlHCnH0yHvzHNyHGz?H";
            String command = buildCommand("reduce", fumen, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getReturnCode()).isGreaterThan(0);
            assertThat(log.getOutput().trim()).isEqualTo("");
        }

        @Test
        void reduce3() throws Exception {
            String fumens = "v115@vhFXKJNJJ0/IWSJTIJCDJ v115@vhFRPJTFJvLJMJJi/IGBJ";
            String command = buildCommand("reduce", fumens, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains("v115@9gBtEeilwwBtDeglRpxwR4Ceg0RpwwR4Dei0JeAgH")
                    .contains("v115@9gili0DeglAtRpQ4g0DeBtRpR4DeAtzhQ4NeAgH");
        }

        @Test
        void reduce4CommentAtHead() throws Exception {
            // 1ページ目のコメントが残る
            String fumens = "v115@vhFXKYZAxXHDBQGDSA1d0ACDYHDBQzuRA1Dq9BlAAA?ANJYZAyXHDBQGDSA1d0ACDYHDBQzuRA1Dq9BlAAAA0/XZAz?XHDBQGDSA1d0ACDYHDBQzuRA1Dq9BlAAAAWSYZA0XHDBQGD?SA1d0ACDYHDBQzuRA1Dq9BlAAAATIYZA1XHDBQGDSA1d0AC?DYHDBQzuRA1Dq9BlAAAACDYZA2XHDBQGDSA1d0ACDYHDBQz?uRA1Dq9BlAAAA";
            String command = buildCommand("reduce", fumens, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains("v115@9gBtEeilwwBtDeglRpxwR4Ceg0RpwwR4Dei0JeAgWZ?AxXHDBQGDSA1d0ACDYHDBQzuRA1Dq9BlAAAA");
        }

        @Test
        void reduce5() throws Exception {
            String fumens = "v115@8gA8HeB8HeB8BeD8CeA8BeE8AeB8whglQpAtwwg0Q4?AeB8TJnvhCTpBlkDxpB";
            String command = buildCommand("reduce", fumens, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains("v115@8gA8HeB8HeB8RpD8ywA8RpE8wwB8JeAgH");
        }

        @Test
        void reduceEmpty() throws Exception {
            String fumens = "v115@vhAAgH";
            String command = buildCommand("reduce", fumens, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput()).contains("v115@vhAAgH");
        }
    }

    @Nested
    class FumenRemoveCommentTest extends UtilFumenUseCaseBaseTest {
        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void removeComment1() throws Exception {
            String fumens = "v115@EhC8HeB8EeF8DeE8JeTKJvhGOrB/pBUsB3qBlpBSMA?AAA";
            String command = buildCommand("remove-comment", fumens, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains("v115@EhC8HeB8EeF8DeE8JeTKJvhGOrB/pBUsB3qBlpBSMA?AAA");
        }

        @Test
        void removeComment2() throws Exception {
            String fumens = "v115@bhA8HeA8whglQpAtwwg0Q4A8AeA8AgWFAwSZrDRAAA?AvhAA4QFAwSZrDSAAAAlhA8AeA8Q4g0wwAtQpglwhAAtFAw?SZrDTAAAAvhCAwSAAA4QFAwSZrDVAAAAAAPFAwSZrDWAAAA?";
            String command = buildCommand("remove-comment", fumens, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput())
                    .contains("v115@bhA8HeA8whglQpAtwwg0Q4A8AeA8AgHvhAA4BlhA8A?eA8Q4g0wwAtQpglwhAAevhCAwDA4BAAA");
        }

        @Test
        void removeCommentEmpty() throws Exception {
            String fumens = "v115@vhAAgH";
            String command = buildCommand("remove-comment", fumens, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput()).contains("v115@vhAAgH");
        }
    }

    @Nested
    class FumenFilterTest extends UtilFumenUseCaseBaseTest {
        @Override
        @BeforeEach
        void setUp() throws IOException {
            super.setUp();
        }

        @Test
        void filterT1() throws Exception {
            String fumens = "v115@vhGWSJJHJSQJXGJVBJUIJTJJ";
            String command = buildCommand("filter", fumens, "-f T");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput()).contains("v115@vhFWSJJnBSwBXmBUoBTpB");
        }

        @Test
        void filter2() throws Exception {
            String fumens = "v115@pgE8CeH8AeI8AeI8AeI8AeI8AeC8JeS0IvhCXqIF4I?JHJ";

            {
                String command = buildCommand("filter", fumens, "-f T");
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput()).contains("v115@pgE8CeH8AeI8AeI8AeI8AeI8AeC8JeS0IvhBXKBJnB?");
            }
            {
                String command = buildCommand("filter", fumens, "-f I");
                Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

                assertThat(log.getOutput()).contains("v115@pgE8CeH8AeI8AeI8AeI8AeI8AeC8JeS0IvhBXKBFYB?");
            }
        }

        @Test
        void filterEmpty() throws Exception {
            String fumens = "v115@vhAAgH";
            String command = buildCommand("filter", fumens, "-f T");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getOutput()).contains("v115@vhAAgH");
        }

        @Test
        void noFilteredPiece() throws Exception {
            String fumens = "v115@vhAAgH";
            String command = buildCommand("filter", fumens, "");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getReturnCode()).isNotEqualTo(0);
        }

        @Test
        void illegalFilteredPiece() throws Exception {
            String fumens = "v115@vhAAgH";
            String command = buildCommand("filter", fumens, "-f K");
            Log log = RunnerHelper.runnerCatchingLog(() -> EntryPointMain.main(command.split(" ")));

            assertThat(log.getReturnCode()).isNotEqualTo(0);
        }
    }
}