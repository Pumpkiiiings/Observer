import { DocsLayout } from 'fumadocs-ui/layouts/docs';
import type { ReactNode } from 'react';
import { baseOptions } from '@/app/layout.config';
import { source } from '@/lib/source';

export default async function Layout(props: { children: ReactNode; params: Promise<{ lang: string }> }) {
  const params = await props.params;
  const tree = (source.pageTree as any)[params.lang] || source.pageTree;

  return (
    <DocsLayout tree={tree} {...baseOptions}>
      {props.children}
    </DocsLayout>
  );
}
