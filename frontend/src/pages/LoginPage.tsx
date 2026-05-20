import { useState, FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Loader2, Library } from 'lucide-react';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('admin@biblio.local');
  const [password, setPassword] = useState('Admin@123');
  const [busy, setBusy] = useState(false);

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setBusy(true);
    try {
      await login(email, password);
      toast.success('Welcome back!');
      navigate('/dashboard');
    } catch (e) {
      // global interceptor toasts
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="min-h-screen grid md:grid-cols-2 bg-slate-50 dark:bg-slate-950">
      <div className="hidden md:flex flex-col justify-between p-12 bg-gradient-to-br from-brand-600 via-brand-700 to-brand-900 text-white">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-white/10 grid place-items-center">
            <Library />
          </div>
          <span className="text-xl font-semibold">Bibliotheque Universitaire</span>
        </div>
        <div>
          <h2 className="text-4xl font-bold leading-tight">Welcome to the modern way of managing your university library.</h2>
          <p className="text-brand-100 mt-4 text-lg">
            Search the catalog, reserve and borrow books, and stay on top of your return deadlines.
          </p>
        </div>
        <div className="text-sm text-brand-200">© {new Date().getFullYear()} Bibliotheque Universitaire</div>
      </div>

      <div className="flex items-center justify-center p-8">
        <form onSubmit={onSubmit} className="w-full max-w-md card p-8 space-y-5">
          <div className="text-center">
            <h1 className="text-2xl font-bold">Sign in</h1>
            <p className="text-slate-500 text-sm mt-1">Access your account</p>
          </div>

          <div>
            <label className="label">Email</label>
            <input className="input" type="email" required value={email}
                   onChange={(e) => setEmail(e.target.value)} autoFocus />
          </div>
          <div>
            <label className="label">Password</label>
            <input className="input" type="password" required value={password}
                   onChange={(e) => setPassword(e.target.value)} />
          </div>

          <button type="submit" className="btn-primary w-full" disabled={busy}>
            {busy && <Loader2 className="animate-spin" size={16} />} Sign in
          </button>

          <p className="text-sm text-center text-slate-500">
            New here? <Link className="text-brand-600 font-medium" to="/register">Create an account</Link>
          </p>

          <div className="text-xs text-slate-400 border-t border-slate-200 dark:border-slate-800 pt-4">
            <p className="font-medium mb-1">Demo accounts</p>
            <p>admin@biblio.local · Admin@123</p>
            <p>librarian@biblio.local · Librarian@123</p>
            <p>etudiant@biblio.local · Etudiant@123</p>
          </div>
        </form>
      </div>
    </div>
  );
}
