import { RootProvider } from 'fumadocs-ui/provider/next';
import { Inter } from 'next/font/google';
import type { ReactNode } from 'react';
import '../globals.css';
import { I18nProvider } from 'fumadocs-ui/contexts/i18n';

const inter = Inter({
  subsets: ['latin'],
});

export default async function Layout(props: {
  children: ReactNode;
  params: Promise<{ lang: string }>;
}) {
  const params = await props.params;
  return (
    <html lang={params.lang} className={inter.className} suppressHydrationWarning>
      <body className="flex flex-col min-h-screen" suppressHydrationWarning>
        <RootProvider>
          <I18nProvider
            locale={params.lang}
            locales={[
              { name: 'English', locale: 'en' },
              { name: 'Español', locale: 'es' },
            ]}
            translations={
              {
                en: {
                  name: 'English',
                  search: 'Search',
                  searchNoResult: 'No results found',
                },
                es: {
                  name: 'Español',
                  search: 'Buscar',
                  searchNoResult: 'No se encontraron resultados',
                  toc: 'En esta página',
                  tocNoHeadings: 'Sin encabezados',
                  lastUpdate: 'Última actualización',
                  chooseLanguage: 'Elegir Idioma',
                  nextPage: 'Siguiente',
                  previousPage: 'Anterior',
                },
              }[params.lang]
            }
          >
            {props.children}
          </I18nProvider>
        </RootProvider>
      </body>
    </html>
  );
}
