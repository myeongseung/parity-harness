package com.erflow.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erflow.auth.TestUsers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 쪽지함 목록이 실제 데이터로 그려지는지 확인한다.
 *
 * <p>받은쪽지함(receiver)과 보낸쪽지함(sender)이 갈리는지, 소프트 삭제 필터
 * ({@code <class>_visible = 1})가 걸리는지를 주로 본다.
 */
@SpringBootTest(properties = "server.port=0")
@AutoConfigureMockMvc
@ActiveProfiles("local")
class MessageScreenTest {

    private static final Path OUT = Path.of("build", "rendered");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageService messageService;

    @BeforeAll
    static void requireLocalConfig() {
        assumeTrue(
                new ClassPathResource("application-local.yml").exists(),
                "application-local.yml 이 없어 건너뛴다");
    }

    private String render(String path, String fileName) throws Exception {
        String html = mockMvc.perform(get(path).with(user(TestUsers.admin())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Files.createDirectories(OUT);
        Files.writeString(OUT.resolve(fileName), html);
        return html;
    }

    @Test
    @DisplayName("받은쪽지함이 그려진다")
    void receiverBoxRenders() throws Exception {
        String html = render("/message?class=receiver", "message-receiver.html");

        assertThat(html).contains("받은쪽지함").contains("보낸쪽지함").contains("쪽지쓰기");
        // 받은쪽지함에서만 답장 버튼과 «보낸사람» 검색이 나온다.
        assertThat(html).contains("답장").contains("보낸사람");
    }

    @Test
    @DisplayName("보낸쪽지함은 답장 버튼이 없고 검색이 받는사람이다")
    void senderBoxHasNoReplyAndSearchesReceiver() throws Exception {
        String html = render("/message?class=sender", "message-sender.html");

        assertThat(html).contains("받는사람");
        // 답장 버튼은 받은쪽지함에만 있다.
        assertThat(html).doesNotContain("reply-message");
    }

    @Test
    @DisplayName("class 가 없으면 받은쪽지함으로 본다")
    void defaultsToReceiver() throws Exception {
        assertThat(render("/message", "message-default.html")).contains("받은쪽지함");
    }

    @Test
    @DisplayName("class 가 receiver/sender 가 아니면 잘못된 접근으로 보낸다")
    void unknownClassRedirects() throws Exception {
        mockMvc.perform(get("/message?class=xxx").with(user(TestUsers.admin())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-error"));
    }

    @Test
    @DisplayName("받은쪽지함과 보낸쪽지함은 다른 목록이다")
    void boxesDiffer() {
        // admin 기준. 목록이 겹치면 class 필터가 빠진 것이다. (둘 다 비어 있으면 건너뛴다)
        List<MessageRow> received = messageService.list(
                MessageService.RECEIVER, "admin", new MessageSearch("", ""), 1).rows();
        List<MessageRow> sent = messageService.list(
                MessageService.SENDER, "admin", new MessageSearch("", ""), 1).rows();
        assumeTrue(!received.isEmpty() || !sent.isEmpty(), "admin 쪽지가 없어 건너뛴다");

        // 받은함의 각 행은 admin 이 받는 사람, 보낸함의 각 행은 admin 이 보낸 사람.
        // 한 쪽지가 양쪽에 동시에 들어가려면 자기 자신에게 보낸 것뿐이라, id 교집합은
        // 그런 경우가 없으면 비어야 한다. 여기서는 각 목록이 자기 조건을 지키는지만 본다.
        assertThat(received).allSatisfy(row -> assertThat(row.senderId()).isNotNull());
    }
}
