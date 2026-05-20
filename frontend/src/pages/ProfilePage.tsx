import { useState, FormEvent } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext';
import { authApi, usersApi } from '../api/endpoints';

export default function ProfilePage() {
  const { user, refresh } = useAuth();
  const [form, setForm] = useState({
    firstName: user?.firstName ?? '',
    lastName: user?.lastName ?? '',
    phone: user?.phone ?? '',
    department: user?.department ?? '',
  });
  const [pwd, setPwd] = useState({ currentPassword: '', newPassword: '' });
  const [busy, setBusy] = useState(false);

  if (!user) return null;

  const save = async (e: FormEvent) => {
    e.preventDefault();
    setBusy(true);
    try {
      await usersApi.update(user.id, form);
      await refresh();
      toast.success('Profile updated');
    } finally {
      setBusy(false);
    }
  };

  const changePassword = async (e: FormEvent) => {
    e.preventDefault();
    setBusy(true);
    try {
      await authApi.changePassword(pwd);
      toast.success('Password updated');
      setPwd({ currentPassword: '', newPassword: '' });
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="grid lg:grid-cols-2 gap-6">
      <form onSubmit={save} className="card p-6 space-y-4">
        <h2 className="text-lg font-semibold">Personal info</h2>

        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="label">First name</label>
            <input className="input" value={form.firstName}
                   onChange={(e) => setForm({ ...form, firstName: e.target.value })} />
          </div>
          <div>
            <label className="label">Last name</label>
            <input className="input" value={form.lastName}
                   onChange={(e) => setForm({ ...form, lastName: e.target.value })} />
          </div>
        </div>
        <div>
          <label className="label">Email</label>
          <input className="input" disabled value={user.email} />
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="label">Phone</label>
            <input className="input" value={form.phone}
                   onChange={(e) => setForm({ ...form, phone: e.target.value })} />
          </div>
          <div>
            <label className="label">Department</label>
            <input className="input" value={form.department}
                   onChange={(e) => setForm({ ...form, department: e.target.value })} />
          </div>
        </div>
        <button className="btn-primary" disabled={busy}>Save changes</button>
      </form>

      <form onSubmit={changePassword} className="card p-6 space-y-4">
        <h2 className="text-lg font-semibold">Change password</h2>
        <div>
          <label className="label">Current password</label>
          <input className="input" type="password" required value={pwd.currentPassword}
                 onChange={(e) => setPwd({ ...pwd, currentPassword: e.target.value })} />
        </div>
        <div>
          <label className="label">New password</label>
          <input className="input" type="password" required minLength={6} value={pwd.newPassword}
                 onChange={(e) => setPwd({ ...pwd, newPassword: e.target.value })} />
        </div>
        <button className="btn-primary" disabled={busy}>Update password</button>
      </form>
    </div>
  );
}
