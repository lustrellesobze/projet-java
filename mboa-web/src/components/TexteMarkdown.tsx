import ReactMarkdown, { type Components } from 'react-markdown'
import remarkGfm from 'remark-gfm'

/**
 * Affiche le texte rédigé par l'IA avec sa mise en forme Markdown :
 * gras, listes, tableaux, titres, liens.
 * Le HTML éventuellement présent dans le texte n'est jamais interprété (sécurité).
 */
const composants: Components = {
  p: ({ children }) => <p className="my-2 leading-relaxed first:mt-0 last:mb-0">{children}</p>,
  strong: ({ children }) => <strong className="font-semibold">{children}</strong>,
  ul: ({ children }) => <ul className="my-2 list-disc space-y-1 pl-6">{children}</ul>,
  ol: ({ children }) => <ol className="my-2 list-decimal space-y-1 pl-6">{children}</ol>,
  li: ({ children }) => <li className="leading-relaxed">{children}</li>,
  h1: ({ children }) => <h3 className="mb-2 mt-4 text-lg font-semibold first:mt-0">{children}</h3>,
  h2: ({ children }) => <h3 className="mb-2 mt-4 text-lg font-semibold first:mt-0">{children}</h3>,
  h3: ({ children }) => <h4 className="mb-2 mt-3 font-semibold first:mt-0">{children}</h4>,
  a: ({ href, children }) => (
    <a href={href} target="_blank" rel="noopener noreferrer" className="font-medium text-vert underline">
      {children}
    </a>
  ),
  code: ({ children }) => <code className="rounded bg-fond px-1.5 py-0.5 font-mono text-[13px]">{children}</code>,
  blockquote: ({ children }) => (
    <blockquote className="my-2 border-l-4 border-or pl-4 text-texte-doux">{children}</blockquote>
  ),
  hr: () => <hr className="my-4 border-bord" />,
  // Un tableau large défile horizontalement au lieu de déborder de la carte (mobile).
  table: ({ children }) => (
    <div className="my-3 overflow-x-auto rounded-lg border border-bord">
      <table className="w-full border-collapse text-sm">{children}</table>
    </div>
  ),
  thead: ({ children }) => <thead className="bg-fond">{children}</thead>,
  th: ({ children }) => (
    <th className="whitespace-nowrap border-b border-bord px-3 py-2 text-left font-semibold">{children}</th>
  ),
  td: ({ children }) => <td className="whitespace-nowrap border-b border-bord px-3 py-2">{children}</td>,
}

export default function TexteMarkdown({ texte }: { texte: string }) {
  return (
    <div className="text-[15px]">
      <ReactMarkdown remarkPlugins={[remarkGfm]} components={composants}>
        {texte}
      </ReactMarkdown>
    </div>
  )
}
