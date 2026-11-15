import type { OutilExecute } from '../api/client'
import type { Textes } from '../i18n'
import { parametresLisibles } from '../utils/outils'
import { IconeCadenas } from './Icones'

/** Panneau de droite « Outils exécutés » : la preuve que les chiffres viennent du code Java. */
export default function PanneauOutils({ t, outils, surligne }: { t: Textes; outils: OutilExecute[]; surligne: boolean }) {
  return (
    <aside
      className={`hidden w-[310px] shrink-0 flex-col gap-4 overflow-y-auto border-l border-bord bg-surface p-5 transition-colors lg:flex ${
        surligne ? 'bg-vert-clair/40' : ''
      }`}
    >
      <div className="flex items-baseline justify-between">
        <h2 className="text-lg font-semibold">{t.outilsExecutes}</h2>
        <span className="text-xs text-texte-doux">{t.cetteConversation}</span>
      </div>
      <p className="text-sm text-texte-doux">{t.explicationOutils}</p>

      {outils.length === 0 ? (
        <p className="rounded-xl border border-dashed border-bord p-4 text-sm text-texte-doux">{t.aucunOutil}</p>
      ) : (
        outils.map((outil, i) => (
          <div key={i} className="rounded-xl border border-vert/25 bg-vert-clair/50 p-4">
            <div className="mb-3 flex items-center justify-between gap-2">
              <code className="break-all font-mono text-[13px]">{outil.nom}</code>
              <span
                className={`shrink-0 rounded-full px-2 py-0.5 text-xs font-semibold text-white ${
                  outil.succes ? 'bg-vert' : 'bg-rouge-texte'
                }`}
              >
                {outil.succes ? t.succes : t.erreur}
              </span>
            </div>
            <dl className="grid grid-cols-[auto_1fr] gap-x-4 gap-y-1 text-sm">
              {parametresLisibles(outil.parametres).map(([cle, valeur]) => (
                <div key={cle} className="contents">
                  <dt className="text-texte-doux">{cle}</dt>
                  <dd className="truncate font-mono text-[13px]">{valeur}</dd>
                </div>
              ))}
              <dt className="text-texte-doux">{t.duree}</dt>
              <dd className="font-mono text-[13px]">{outil.dureeMs} ms</dd>
            </dl>
          </div>
        ))
      )}

      <p className="flex gap-2 rounded-xl border border-dashed border-bord p-4 text-sm text-texte-doux">
        <span className="mt-0.5 shrink-0">
          <IconeCadenas />
        </span>
        {t.confidentialite}
      </p>
    </aside>
  )
}
