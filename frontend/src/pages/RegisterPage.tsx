import { useState, FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Loader2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    firstName: '', lastName: '', email: '', password: '',
    matricule: '', phone: '', department: '',
  });
  const [busy, setBusy] = useState(false);

  const onChange = (k: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm({ ...form, [k]: e.target.value });

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setBusy(true);
    try {
      await register(form);
      toast.success('Account created');
      navigate('/dashboard');
    } catch {
      // toast handled
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="min-h-screen grid place-items-center p-6 bg-slate-50 dark:bg-slate-950">
      <form onSubmit={onSubmit} className="card p-8 w-full max-w-2xl space-y-4">
        <h1 className="text-2xl font-bold text-center">Create your account</h1>

        <div className="grid sm:grid-cols-2 gap-4">
          <div>
            <label className="label">First name *</label>
            <input className="input" required value={form.firstName} onChange={onChange('firstName')} />
          </div>
          <div>
            <label className="label">Last name *</label>
            <input className="input" required value={form.lastName} onChange={onChange('lastName')} />
          </div>
          <div>
            <label className="label">Email *</label>
            <input className="input" type="email" required value={form.email} onChange={onChange('email')} />
          </div>
          <div>
            <label className="label">Password *</label>
            <input className="input" type="password" required minLength={6}
                   value={form.password} onChange={onChange('password')} />
          </div>
          <div>
            <label className="label">Matricule</label>
            <input className="input" value={form.matricule} onChange={onChange('matricule')} />
          </div>
          <div>
            <label className="label">Department</label>
            <input className="input" value={form.department} onChange={onChange('department')} />
          </div>
          <div className="sm:col-span-2">
            <label className="label">Phone</label>
            <input className="input" value={form.phone} onChange={onChange('phone')} />
          </div>
        </div>

        <button className="btn-primary w-full" disabled={busy}>
          {busy && <Loader2 size={16} className="animate-spin" />} Create account
        </button>

        <p className="text-sm text-center text-slate-500">
          Already have an account? <Link to="/login" className="text-brand-600 font-medium">Sign in</Link>
        </p>
      </form>
    </div>
  );
}
