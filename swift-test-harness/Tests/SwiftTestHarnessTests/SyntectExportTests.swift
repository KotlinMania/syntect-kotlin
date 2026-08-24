import Testing
import Syntect

@Suite("Syntect Swift Export Tests")
struct SyntectExportTests {
    @Test("Swift module loads and verifies basic constants and operations")
    func testModuleExports() {
        let black = highlighting.Color.Companion.shared.BLACK
        let white = highlighting.Color.Companion.shared.WHITE
        #expect(black.r == 0)
        #expect(black.g == 0)
        #expect(black.b == 0)
        #expect(black.a == 255)

        #expect(white.r == 255)
        #expect(white.g == 255)
        #expect(white.b == 255)
        #expect(white.a == 255)

        let bold = highlighting.FontStyle.Companion.shared.BOLD
        let italic = highlighting.FontStyle.Companion.shared.ITALIC
        let empty = highlighting.FontStyle.Companion.shared.empty()
        let all = highlighting.FontStyle.Companion.shared.all()

        #expect(bold.bits == 1)
        #expect(italic.bits == 4)
        #expect(empty.bits == 0)
        #expect(all.bits == 7)

        let defaultStyle = highlighting.Style.Companion.shared.default()
        #expect(defaultStyle.foreground == black)
        #expect(defaultStyle.background == white)

        let scope = parsing.Scope.Companion.shared.fromString(s: "source.kotlin")
        #expect(scope.len() == 2)

        let stack = parsing.ScopeStack.Companion.shared.fromString(s: "source.kotlin storage.type")
        #expect(stack.len() == 2)
        #expect(!stack.isEmpty())

        let builder = parsing.SyntaxSetBuilder()
        let syntaxSet = builder.build()
        #expect(syntaxSet.syntaxes.isEmpty)
    }
}
