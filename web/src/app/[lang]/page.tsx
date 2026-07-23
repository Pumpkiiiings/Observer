import Link from 'next/link';
import { Pixelify_Sans } from 'next/font/google';

const pixelify = Pixelify_Sans({ subsets: ['latin'] });

export default async function HomePage(props: { params: Promise<{ lang: string }> }) {
  const params = await props.params;
  
  return (
    <div 
      className="flex min-h-screen flex-col bg-black text-white relative"
      style={{
        backgroundImage: "linear-gradient(to bottom, rgba(0,0,0,0.6), rgba(0,0,0,0.8)), url('https://i.ibb.co/C5HGMrpN/poolrooms.png')",
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundAttachment: 'fixed'
      }}
    >
      {/* Navbar */}
      <nav className="w-full fixed top-0 left-0 z-50 backdrop-blur-md bg-black/40 border-b border-white/10 px-6 py-4 flex justify-between items-center">
        <div className={`flex items-center gap-3 text-xl font-bold tracking-widest ${pixelify.className}`}>
          <img src="https://i.ibb.co/202L5FHr/icon-png.png" alt="Observer Logo" className="w-8 h-8 rounded-full" />
          OBSERVER
        </div>
        <div className="flex gap-6 items-center">
          <Link href={`/${params.lang}/docs`} className="hover:text-neutral-300 transition-colors">
            {params.lang === 'es' ? 'Documentación' : 'Docs'}
          </Link>
          <Link href="https://github.com/Pumpkiiiings" target="_blank" className="hover:text-neutral-300 transition-colors">
            {params.lang === 'es' ? 'Otros Proyectos' : 'Other Projects'}
          </Link>
          <Link href="https://github.com/Pumpkiiiings/Observer" target="_blank" className="hover:text-neutral-300 transition-colors">
            GitHub
          </Link>
        </div>
      </nav>

      {/* Main Content */}
      <main className="flex flex-1 flex-col items-center justify-center p-6 text-center z-10 mt-16">
        <img 
          src="https://i.ibb.co/202L5FHr/icon-png.png" 
          alt="Observer Logo" 
          className="w-32 h-32 md:w-48 md:h-48 mb-8 rounded-[2rem] shadow-2xl drop-shadow-[0_0_15px_rgba(255,255,255,0.3)]"
        />
        <h1 className={`text-6xl md:text-8xl font-bold tracking-[0.2em] mb-6 drop-shadow-lg ${pixelify.className}`}>
          O B S E R V E R
        </h1>
        <p className="text-xl md:text-2xl text-neutral-200 mb-12 max-w-3xl drop-shadow-md">
          {params.lang === 'es' ? 'El mejor framework cliente-servidor para servidores de Paper.' : 'The best client-server side framework for paper servers.'}
        </p>
        
        <div className="flex gap-4 justify-center">
          <Link 
            href={`/${params.lang}/docs`}
            className="px-8 py-4 rounded-xl bg-white/20 hover:bg-white/30 border border-white/20 backdrop-blur-md transition-all font-semibold text-lg drop-shadow-lg"
          >
            {params.lang === 'es' ? 'Empezar' : 'Get Started'}
          </Link>
          <Link 
            href="https://github.com/Pumpkiiiings/Observer"
            target="_blank"
            className="px-8 py-4 rounded-xl bg-black/50 hover:bg-black/70 border border-white/20 backdrop-blur-md transition-all font-semibold text-lg drop-shadow-lg"
          >
            GitHub
          </Link>
        </div>
      </main>
    </div>
  );
}
